package cn.edu.pku.EOSCN.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


import cn.edu.pku.EOSCN.util.XMLUtils;
/**
 * 存储开源项目基本信息的类
 * @author 张灵箫
 *
 */

public class Project {

	public static final String JAVA = "Java";
	public static final String CPP = "C++";
	public static final String C = "C";
	private String name;
	private String orgName;
	private String projectName;
	private String hostUrl = "";
	private String programmingLanguage = JAVA;
	private String description = "";
	private String uuid;	
	
	/**
	 * @author 张灵箫
	 * 请用该构造方法得到uuid
	 */
	public Project(String _name, String _hostUrl) {
		name = _name;
		hostUrl = _hostUrl;
		uuid = UUID.randomUUID().toString();
	}
	
	/**
	 * @author 张灵箫
	 * 用该方法从xml文件得到新项目，包含uuid
	 * @param xmlFile xml文件路径
	 */
	public Project(String xmlFile) {
		Project project;
		project = XMLUtils.getProjectFromXmlFile(xmlFile);
		name = project.getName();
		hostUrl = project.getHostUrl();
		programmingLanguage = project.getProgrammingLanguage();
		description = project.getDescription();
		uuid = UUID.randomUUID().toString();
	}
	/**
	 * @author 张灵箫
	 * 供javabean使用，不要手工调用该构造方法，否则无法得到uuid
	 */
	public Project() {
	}

	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getHostUrl() {
		return hostUrl;
	}
	public void setHostUrl(String hostUrl) {
		this.hostUrl = hostUrl;
	}
	public String getProgrammingLanguage() {
		return programmingLanguage;
	}
	public void setProgrammingLanguage(String programmingLanguage) {
		this.programmingLanguage = programmingLanguage;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
}
