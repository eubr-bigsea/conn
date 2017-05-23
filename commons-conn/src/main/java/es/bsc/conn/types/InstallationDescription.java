package es.bsc.conn.types;

public class InstallationDescription {

	private String installDir;
	private String appDir;
	private String classpath;
	private String pythonPath;
	private String libraryPath;
	private String workingDir;
	private int limitOfTasks;

	public InstallationDescription (String installDir, String appDir, String classpath, String pythonpath, 
			String libraryPath, String workingDir, int limitOfTasks){
		this.installDir = installDir;
		this.appDir = appDir;
		this.classpath = classpath;
		this.pythonPath = pythonpath;
		this.libraryPath = libraryPath;
		this.workingDir = workingDir;
		this.limitOfTasks = limitOfTasks;
	}

	/**
	 * @return the limitOfTasks
	 */
	public int getLimitOfTasks() {
		return limitOfTasks;
	}

	/**
	 * @param limitOfTasks the limitOfTasks to set
	 */
	public void setLimitOfTasks(int limitOfTasks) {
		this.limitOfTasks = limitOfTasks;
	}

	/**
	 * @return the installDir
	 */
	public String getInstallDir() {
		return installDir;
	}

	/**
	 * @param installDir the installDir to set
	 */
	public void setInstallDir(String installDir) {
		this.installDir = installDir;
	}

	/**
	 * @return the appDir
	 */
	public String getAppDir() {
		return appDir;
	}

	/**
	 * @param appDir the appDir to set
	 */
	public void setAppDir(String appDir) {
		this.appDir = appDir;
	}

	/**
	 * @return the classpath
	 */
	public String getClasspath() {
		return classpath;
	}

	/**
	 * @param classpath the classpath to set
	 */
	public void setClasspath(String classpath) {
		this.classpath = classpath;
	}

	/**
	 * @return the pythonPath
	 */
	public String getPythonPath() {
		return pythonPath;
	}

	/**
	 * @param pythonPath the pythonPath to set
	 */
	public void setPythonPath(String pythonPath) {
		this.pythonPath = pythonPath;
	}

	/**
	 * @return the libraryPath
	 */
	public String getLibraryPath() {
		return libraryPath;
	}

	/**
	 * @param libraryPath the libraryPath to set
	 */
	public void setLibraryPath(String libraryPath) {
		this.libraryPath = libraryPath;
	}

	/**
	 * @return the workingDir
	 */
	public String getWorkingDir() {
		return workingDir;
	}

	/**
	 * @param workingDir the workingDir to set
	 */
	public void setWorkingDir(String workingDir) {
		this.workingDir = workingDir;
	}
	
	
}
