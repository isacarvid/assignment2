package models;

public class BuildStatus {
	private String cloneStatus;

	public String getBuildStatus() {
		return buildStatus;
	}

	public void setBuildStatus(String buildStatus) {
		this.buildStatus = buildStatus;
	}

	public String getTestStatus() {
		return testStatus;
	}

	public void setTestStatus(String testStatus) {
		this.testStatus = testStatus;
	}

	public String getCloneStatus() {
		return cloneStatus;
	}

	public void setCloneStatus(String cloneStatus) {
		this.cloneStatus = cloneStatus;
	}

	private String buildStatus;
	private String testStatus;
	boolean successBuild;
	boolean successTest;

	/**
	 * Check in build and test status if successful
	 */
	public void setSuccessBuild() {
		if (cloneStatus.isEmpty() || testStatus.isEmpty() || buildStatus.isEmpty()) {
			successBuild = false;
		}
		if (buildStatus.contains("BUILD SUCCESSFUL")) {
			successBuild = true;
		} else {
			successBuild = false;
		}
	}

	public void setSuccessTest() {
		if (cloneStatus.isEmpty() || testStatus.isEmpty() || buildStatus.isEmpty()) {
			successTest = false;
		}
		if (testStatus.contains("BUILD SUCCESSFUL") ) {
			successTest = true;
		} else {
			successTest = false;
		}
	}
	public boolean isSuccessBuild() {
		return successBuild;
	}
	public boolean isSuccessTest() {
		return successTest;
	}

}
