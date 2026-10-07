package com.maan.eway.jasper.service.impl;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.jndi.JndiObjectFactoryBean;

@Configuration
public class JasperConfiguration {
	
	@Value("${draft.file.path}")
	private String draftPath;
	
	@Value("${image.path}")
	private String imagePath;
	
	@Value("${policy.file.path}")
	private String policyPath;
	
	@Value("${proposal.file.path}")
	private String proposalPath;
	
	@Value("${jasper.datasourceby.jndi}")
	private String datasourcebyjndi;
	
	@Value("${jasper.file.path}")
	private String jasperpath;

	 
	// Enable This Value For run in Jndi Server

//@Value("${spring.datasource.jndi-name}")   //jndi uncommand for revion
	private String jndiDatasource;
	
	public String getDraftPath() {
		return draftPath;		
	}
	
	public String getPolicyPath() {
		return policyPath;		
	}
	
	public String getProposalPath() {
		return proposalPath;		
	}
	
	private static String classpathof;
	static{
		classpathof=(JasperConfiguration.class).getProtectionDomain().getCodeSource().getLocation().getPath();
	}
	public String getImagePath() {
		return imagePath;
		// return (classpathof+"report/images/").replaceAll("%20", " ");
	} 
	
	private DataSource getDataSourceFromJNDI()  { 
    	try {
    		JndiObjectFactoryBean bean = new JndiObjectFactoryBean();    
    		bean.setJndiName(jndiDatasource);     		
    		bean.setProxyInterface(DataSource.class);
    		bean.setLookupOnStartup(false);
    		bean.afterPropertiesSet();
    		return (DataSource) bean.getObject();
		} catch(Exception e) {
			e.printStackTrace();
		}
    	return null;
	}
	
	public String getJasperFilePath() {
		String classPath = this.getClass().getClassLoader().getResource("").getPath();
		classPath = classPath.substring(1, classPath.length()-0);
		return classPath;
		//return jasperpath;
	}
	
	// Enable This 4 Value For run in Application properties 
	@Value("${spring.datasource.driver-class-name}")
	private String driverclassname;
	@Value("${spring.datasource.url}")
	private String datasourceurl;
	@Value("${spring.datasource.username}")
	private String datausername;
	@Value("${spring.datasource.password}")
	private String datapassword;
	
	
	
	
	
	
	private DataSource getDataSourceFromSpring()  { 
    	try {
    			DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
    	        dataSourceBuilder.driverClassName(driverclassname);
    	        dataSourceBuilder.url(datasourceurl);
    	        dataSourceBuilder.username(datausername);
    	        dataSourceBuilder.password(datapassword);
    	        return dataSourceBuilder.build();    	
		} catch(Exception e) {
			e.printStackTrace();
		}
    	return null;
	}
	
	 
	public DataSource getDataSourceForJasper() {
		if("N".equals(datasourcebyjndi))
				return getDataSourceFromSpring();
		else 
				return getDataSourceFromJNDI();
	}
	
}
