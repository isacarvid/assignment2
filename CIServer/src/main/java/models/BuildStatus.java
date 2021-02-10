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
	boolean success;

	/**
	 * Check in build and test status if successful
	 */
	public void checkSuccess() {
		if (cloneStatus.isEmpty() || testStatus.isEmpty() || buildStatus.isEmpty()) {
			success = false;
		}
		if (testStatus.contains("BUILD SUCCESSFUL") && buildStatus.contains("BUILD SUCCESSFUL")) {
			success = true;
		} else {
			success = false;
		}
	}

	public boolean isSuccess() {
		return success;
	}

}
