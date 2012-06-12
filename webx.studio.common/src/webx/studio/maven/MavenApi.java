package webx.studio.maven;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.Maven;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.manager.WagonManager;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryFactory;
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy;
import org.apache.maven.artifact.repository.DefaultArtifactRepository;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.artifact.resolver.filter.ScopeArtifactFilter;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.monitor.event.DefaultEventDispatcher;
import org.apache.maven.monitor.event.EventDispatcher;
import org.apache.maven.profiles.DefaultProfileManager;
import org.apache.maven.profiles.ProfileManager;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectUtils;
import org.apache.maven.project.artifact.ProjectArtifactFactory;
import org.apache.maven.settings.MavenSettingsBuilder;
import org.apache.maven.settings.Profile;
import org.apache.maven.settings.Repository;
import org.apache.maven.settings.RepositoryPolicy;
import org.apache.maven.settings.Settings;
import org.apache.maven.wagon.observers.Debug;
import org.codehaus.classworlds.ClassWorld;
import org.codehaus.plexus.embed.Embedder;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.LoggerManager;
import org.codehaus.plexus.util.cli.CommandLineUtils;

import webx.studio.StudioCommonPlugin;
import webx.studio.utils.PathUtil;

/**
 * Maven api, all operation of maven use this class
 *
 * @author zhihua.hanzh@alibaba-inc.com
 */
public class MavenApi {

	private Embedder embedder;

	private File settingFile;

	private boolean initialized = false;

	public void setSettingFile(File file) {

		if (ObjectUtils.equals(settingFile, file))
			return;

		settingFile = file;
	}

	public void init() throws Exception {
		if (initialized)
			return;
		embedder = new Embedder();
		ClassWorld classWorld = new ClassWorld("plexus.core", Thread
				.currentThread().getContextClassLoader());
		embedder.start(classWorld);
		initialized = true;
	}

	public void reinit() throws Exception {
		if(embedder == null)
			return;
		embedder.stop();
		initialized = false;
		init();
	}

	public void execute(List<String> goals) throws Exception {
		init();
		LoggerManager loggerManager = (LoggerManager) embedder
				.lookup(LoggerManager.ROLE);
		Settings settings = buildSettings();
		EventDispatcher eventDispatcher = new DefaultEventDispatcher();
		Properties executionProperties = new Properties();
		Properties userProperties = new Properties();
		prepareProperties(executionProperties, userProperties);
		ProfileManager profileManager = new DefaultProfileManager(
				embedder.getContainer(), settings, executionProperties);
		MavenExecutionRequest request = createRequest(settings,
				eventDispatcher, profileManager, executionProperties,
				userProperties, false, goals);
		Maven maven = createMavenInstance();
		embedder.release(loggerManager);
		maven.execute(request);
	}

	private MavenProject getMavenProject(File pomFile,
			String localRepositoryPath) throws Exception {
		if (pomFile == null || !pomFile.exists() || !pomFile.isFile()
				|| StringUtils.isBlank(localRepositoryPath))
			return null;

		init();
		WagonManager manager = (WagonManager) embedder
				.lookup(WagonManager.ROLE);
		manager.setOnline(false);
		MavenProjectBuilder builder = (MavenProjectBuilder) embedder
				.lookup(MavenProjectBuilder.ROLE);
		ArtifactRepositoryLayout repositoryLayout = (ArtifactRepositoryLayout) embedder
				.lookup(ArtifactRepositoryLayout.ROLE, "default");
		if (!localRepositoryPath.startsWith("file:")) {
			localRepositoryPath = "file://" + localRepositoryPath;
		}
		ArtifactRepository localRespository = new DefaultArtifactRepository(
				"local", localRepositoryPath, repositoryLayout);
		MavenProject project = builder.build( pomFile, localRespository, null, false );
		Artifact projectArtifact = project.getArtifact();

        Map managedVersions = project.getManagedVersionMap();

        ProjectArtifactFactory artifactFactory = (ProjectArtifactFactory)embedder.lookup("org.apache.maven.project.artifact.ProjectArtifactFactory");
        project.setDependencyArtifacts( project.createArtifacts( artifactFactory, null, null ) );

        ArtifactResolver artifactResolver = (ArtifactResolver)embedder.lookup(ArtifactResolver.ROLE);
        ArtifactMetadataSource artifactMetadataSource = (ArtifactMetadataSource) embedder.lookup( ArtifactMetadataSource.ROLE );
        ArtifactResolutionResult result = artifactResolver.resolveTransitively( project.getDependencyArtifacts(),
                                                                                projectArtifact, managedVersions,
                                                                                localRespository,
                                                                                project.getRemoteArtifactRepositories(),
                                                                                artifactMetadataSource,new ScopeArtifactFilter(DefaultArtifact.SCOPE_RUNTIME) );

        project.setArtifacts( result.getArtifacts() );
        return project;
	}

	public List<String> getTestAndProvidedPaths(File pomFile,
			String localRepositoryPath) throws Exception {
		List<String> paths = new ArrayList<String>();
		MavenProject mp = getMavenProject(pomFile, localRepositoryPath);
		if (mp == null) {
			return paths;
		}
		for (Iterator iter = mp.getArtifacts().iterator(); iter.hasNext();) {
			Artifact artifact = (Artifact) iter.next();
			if ((Artifact.SCOPE_PROVIDED.equals(artifact.getScope()))
					|| (Artifact.SCOPE_TEST.equals(artifact.getScope()))) {
				paths.add(artifact.getFile().getCanonicalPath());
			}
		}
		return paths;
	}

	public List<String> getNonTestAndProvidedPaths(File pomFile,
			String localRepositoryPath) throws Exception {
		List<String> paths = new ArrayList<String>();
		MavenProject mp = getMavenProject(pomFile, localRepositoryPath);
		if (mp == null) {
			return paths;
		}
		for (Iterator iter = mp.getArtifacts().iterator(); iter.hasNext();) {
			Artifact artifact = (Artifact) iter.next();
			if ((Artifact.SCOPE_PROVIDED.equals(artifact.getScope()))
					|| (Artifact.SCOPE_TEST.equals(artifact.getScope()))) {
				System.err.println("skip "
						+ artifact.getFile().getCanonicalPath()
						+ ",because its scope is provided or test!");
			} else {
				paths.add(artifact.getFile().getCanonicalPath());
			}
		}
		return paths;
	}

	public String getLocalRepositoryPath() {

		try {
			init();
			return buildSettings().getLocalRepository();
		} catch (Exception e) {

			return null;
		}
	}

	public ArtifactRepository getLocalRepository() {
		try {
			init();
			ArtifactRepositoryLayout repositoryLayout = (ArtifactRepositoryLayout) embedder
					.lookup(ArtifactRepositoryLayout.ROLE, "default");

			String url = getLocalRepositoryPath();

			if (!url.startsWith("file:")) {
				url = "file://" + url;
			}
			return new DefaultArtifactRepository("local", url, repositoryLayout);
		} catch (Exception e) {
			return null;
		}
	}

	private Settings buildSettings() throws Exception {

		configureMavenHome();
		MavenSettingsBuilder settingsBuilder = (MavenSettingsBuilder) embedder
				.lookup(MavenSettingsBuilder.ROLE);
		if (settingFile == null) {
			File defaultUserSettingsFile = PathUtil.getFile(
					"${user.home}/.m2/settings.xml", "user.home",
					"shouldnotexistproperty");
			if (defaultUserSettingsFile != null
					&& defaultUserSettingsFile.exists()
					&& defaultUserSettingsFile.isFile()) {
				return settingsBuilder.buildSettings(false);
			}
			return settingsBuilder.buildSettings(
					StudioCommonPlugin.getMavenSetting(), false);
		} else {
			return settingsBuilder.buildSettings(settingFile, false);
		}
	}

	private void configureMavenHome() throws IOException {

		String mavenHome = System.getProperty("maven.home");
		if (mavenHome == null) {
			mavenHome = CommandLineUtils.getSystemEnvVars().getProperty(
					"M2_HOME");
			if (StringUtils.isNotBlank(mavenHome))
				System.setProperty("maven.home", mavenHome);
		}
	}

	private MavenExecutionRequest createRequest(Settings settings,
			EventDispatcher eventDispatcher, ProfileManager profileManager,
			Properties executionProperties, Properties userProperties,
			boolean showErrors, List<String> goals) throws Exception {
		MavenExecutionRequest request;

		ArtifactRepository localRepository = createLocalRepository(settings);

		File userDir = new File(System.getProperty("user.dir"));

		request = new DefaultMavenExecutionRequest(localRepository, settings,
				eventDispatcher, goals, userDir.getPath(), profileManager,
				executionProperties, userProperties, showErrors);

		return request;
	}

	private ArtifactRepository createLocalRepository(Settings settings)
			throws Exception {

		ArtifactRepositoryLayout repositoryLayout = (ArtifactRepositoryLayout) embedder
				.lookup(ArtifactRepositoryLayout.ROLE, "default");

		String url = settings.getLocalRepository();

		if (!url.startsWith("file:")) {
			url = "file://" + url;
		}
		return new DefaultArtifactRepository("local", url, repositoryLayout);
	}

	private Maven createMavenInstance() throws Exception {
		WagonManager wagonManager = (WagonManager) embedder
				.lookup(WagonManager.ROLE);
		wagonManager.setDownloadMonitor(new Debug());

		return (Maven) embedder.lookup(Maven.ROLE);
	}

	private void prepareProperties(Properties executionProperties,
			Properties userProperties) {
		try {
			Properties envVars = CommandLineUtils.getSystemEnvVars();
			Iterator i = envVars.entrySet().iterator();
			while (i.hasNext()) {
				Entry e = (Entry) i.next();
				executionProperties.setProperty("env." + e.getKey().toString(),
						e.getValue().toString());
			}
			executionProperties.putAll(System.getProperties());
		} catch (Exception e) {
			// ignore
		}

	}

	public Artifact resvoleArtifact(String groupId, String artifactId,
			String version) throws Exception {
		init();
		LoggerManager loggerManager = (LoggerManager) embedder
		.lookup(LoggerManager.ROLE);
 loggerManager.setThreshold(Logger.LEVEL_DEBUG);
		String default_version = "[0.0,)";
		if(StringUtils.isBlank(version)){
			version = default_version;
		}
		ArtifactMetadataSource artifactMetadataSource = (ArtifactMetadataSource) embedder
				.lookup(ArtifactMetadataSource.ROLE);

		ArtifactFactory artifactFactory = (ArtifactFactory) embedder.lookup(ArtifactFactory.ROLE);
		Artifact artifact = artifactFactory.createArtifact(groupId, artifactId,version, Artifact.SCOPE_COMPILE, "jar");

		ArtifactResolver artifactResolver = (ArtifactResolver) embedder.lookup(ArtifactResolver.ROLE);

		ArtifactRepositoryLayout repositoryLayout = (ArtifactRepositoryLayout) embedder.lookup(ArtifactRepositoryLayout.ROLE, "default");
		String localRepositoryPath = getLocalRepositoryPath();
		if (!localRepositoryPath.startsWith("file:")) {
			localRepositoryPath = "file://" + localRepositoryPath;
		}
		ArtifactRepository localRespository = new DefaultArtifactRepository("local", localRepositoryPath, repositoryLayout);

		ArtifactRepositoryFactory artifactRepositoryFactory = (ArtifactRepositoryFactory) embedder.lookup(ArtifactRepositoryFactory.ROLE);

		List<ArtifactRepository> remoteRepositories = ProjectUtils
				.buildArtifactRepositories(
						getRemoteRepositoryModelsFormSetting(),
						artifactRepositoryFactory, embedder.getContainer());

		ArtifactRepositoryPolicy policy = new ArtifactRepositoryPolicy(true,
                ArtifactRepositoryPolicy.UPDATE_POLICY_DAILY,
                ArtifactRepositoryPolicy.CHECKSUM_POLICY_WARN);

		remoteRepositories.add(artifactRepositoryFactory.createArtifactRepository("dubbo.staging",
                "http://repo.alibaba-inc.com/mvn/staging/platform/dubbo/", repositoryLayout, policy,
                policy));

		if (StringUtils.equals(default_version,version)) {

			List versions = artifactMetadataSource.retrieveAvailableVersions(
					artifact, localRespository, remoteRepositories);

			ArtifactVersion latest = null;

			if (versions != null && !versions.isEmpty()) {

				latest = (ArtifactVersion)Collections
						.max((Collection<ArtifactVersion>) versions);

			}
			artifact.setResolvedVersion(latest.toString());
			artifact.setBaseVersion(latest.toString());
			artifact.setVersion(latest.toString());
		}
		artifactResolver
				.resolve(artifact, remoteRepositories, localRespository);
		return artifact;
	}

	public List<org.apache.maven.model.Repository> getRemoteRepositoryModelsFormSetting()
			throws Exception {
		init();
		Settings settings = buildSettings();
		List<String> aps = settings.getActiveProfiles();
		List<Profile> profiles = settings.getProfiles();
		List<org.apache.maven.model.Repository> repostorys = new ArrayList<org.apache.maven.model.Repository>();
		for (String ap : aps) {
			for (Profile profile : profiles) {
				if (StringUtils.equals(ap, profile.getId())) {
					for (Repository pr : profile.getRepositories()) {
						repostorys.add(convertFromSettingsRepository(pr));
					}
				}
			}
		}
		return repostorys;
	}

	private org.apache.maven.model.Repository convertFromSettingsRepository(
			Repository settingsRepo) {
		org.apache.maven.model.Repository repo = new org.apache.maven.model.Repository();

		repo.setId(settingsRepo.getId());
		repo.setLayout(settingsRepo.getLayout());
		repo.setName(settingsRepo.getName());
		repo.setUrl(settingsRepo.getUrl());

		if (settingsRepo.getSnapshots() != null) {
			repo.setSnapshots(convertRepositoryPolicy(settingsRepo
					.getSnapshots()));
		}
		if (settingsRepo.getReleases() != null) {
			repo.setReleases(convertRepositoryPolicy(settingsRepo.getReleases()));
		}

		return repo;
	}

	private org.apache.maven.model.RepositoryPolicy convertRepositoryPolicy(
			RepositoryPolicy settingsPolicy) {
		org.apache.maven.model.RepositoryPolicy policy = new org.apache.maven.model.RepositoryPolicy();
		policy.setEnabled(settingsPolicy.isEnabled());
		policy.setUpdatePolicy(settingsPolicy.getUpdatePolicy());
		policy.setChecksumPolicy(settingsPolicy.getChecksumPolicy());
		return policy;
	}

}
