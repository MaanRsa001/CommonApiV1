
package com.maan.eway;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.apache.tomcat.util.buf.StringUtils;
import org.cache2k.extra.spring.SpringCache2kCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.maan.eway.req.calcengine.CalcEngine;
import com.maan.eway.res.calc.RatingInfo;
import com.maan.eway.workflow.dto.WorkEngine;
@Configuration
@EnableCaching
public class CachingConfig   {
	private SimpleDateFormat DD_MM_YYYY = new SimpleDateFormat("dd/MM/yyyy")  ;
	  @Bean(name = "catcheManager" ,destroyMethod = "destroy")
	  public CacheManager  cacheManager() {
	    return new SpringCache2kCacheManager()
	      .defaultSetup(b->b.entryCapacity(2000))
	      .addCaches(
	        b->b.name("RatingType").expireAfterWrite(1, TimeUnit.MINUTES).entryCapacity(700L).permitNullValues(false),
	        b->b.name("ProductType").expireAfterWrite(1, TimeUnit.MINUTES).entryCapacity(100L).permitNullValues(false),
	        b->b.name("loadTax").expireAfterWrite(1, TimeUnit.MINUTES).entryCapacity(500L).permitNullValues(true),
	        b->b.name("loadProRata").expireAfterWrite(1, TimeUnit.MINUTES).entryCapacity(500L).permitNullValues(true),
	        b->b.name("LoadConstant").expireAfterWrite(1, TimeUnit.MINUTES).entryCapacity(500L).permitNullValues(true),
	        b->b.name("ProductToRawtable").expireAfterWrite(1, TimeUnit.MINUTES).entryCapacity(500L).permitNullValues(false),
	        b->b.name("EndtMasterData").expireAfterWrite(1, TimeUnit.MINUTES).entryCapacity(500L).permitNullValues(false),
	        b->b.name("getCachedRatingFields").expireAfterWrite(1, TimeUnit.MINUTES).entryCapacity(1000L).permitNullValues(false),
	        b->b.name("loadfactorOnlyquery").expireAfterWrite(1, TimeUnit.SECONDS).entryCapacity(1000L).permitNullValues(false),
	        b->b.name("countfactorOnlyquery").expireAfterWrite(1, TimeUnit.MINUTES).entryCapacity(1000L).permitNullValues(false),
	        b->b.name("currencyDecimalFormat").expireAfterWrite(1, TimeUnit.MINUTES).entryCapacity(1000L).permitNullValues(false),
	        b->b.name("fetchAlipaRating").expireAfterWrite(2, TimeUnit.MINUTES).entryCapacity(10L).permitNullValues(false),
	        b->b.name("collectCommissionDetails").expireAfterWrite(2, TimeUnit.MINUTES).entryCapacity(10L).permitNullValues(false),
	        b->b.name("collectProductsFromLoginId").expireAfterWrite(1, TimeUnit.MINUTES).entryCapacity(2L).permitNullValues(false),
	        b->b.name("collectSectionMaster").expireAfterWrite(5, TimeUnit.MINUTES).entryCapacity(100L).permitNullValues(false),
	        b->b.name("collectBranchMaster").expireAfterWrite(5, TimeUnit.MINUTES).entryCapacity(100L).permitNullValues(false),
	        b->b.name("excludedTax").expireAfterWrite(1, TimeUnit.MINUTES).entryCapacity(1000L).permitNullValues(true),
	        b->b.name("FlowFieldData").expireAfterWrite(1, TimeUnit.MINUTES).entryCapacity(1500L).permitNullValues(true),
	        b->b.name("DistinctQueryId").expireAfterWrite(15, TimeUnit.MINUTES).entryCapacity(100L).permitNullValues(false),
	        b->b.name("AzentoURL").expireAfterWrite(15, TimeUnit.MINUTES).entryCapacity(100L).permitNullValues(false),
	        b->b.name("AzentoToken").expireAfterWrite(1, TimeUnit.MINUTES).entryCapacity(100L).permitNullValues(false)
	        
	        )
	      
	      ;
		
	  } 
	

	
	    @Bean
		public KeyGenerator ratingTypeKeyGen() {
			
		return new KeyGenerator() {
			@Override
			public Object generate(Object target, Method method, Object... params) {
				CalcEngine e=(CalcEngine)params[0];
				String s=(String) params[1]; 
				String string = new StringBuilder().append(e.getInsuranceId())
				.append(e.getProductId())
				.append(s)
				.append(e.getBranchCode())
				.append("rating")
				.append(e.getSectionId())
				.toString();
				return string;
			}
			
		};
	}
	    @Bean
		public KeyGenerator productTypeKeyGen() {
			
		return new KeyGenerator() {
			@Override
			public Object generate(Object target, Method method, Object... params) {
				CalcEngine e=(CalcEngine)params[0]; 
				String string = new StringBuilder().append(e.getInsuranceId())
				.append(e.getProductId())
				.append(e.getSectionId())
				.append("producttype")
				.toString();
				return string;
			}
			
		};
	}
	    
	    
	    	@Bean
	    	public KeyGenerator loadTaxKeyGen() {
	    		return new KeyGenerator() {
	    			@Override
	    			public Object generate(Object target, Method method, Object... params) {
	    				CalcEngine e=(CalcEngine)params[0];
	    				List<String> taxFor=(List<String>) params[1];
	    				String string = new StringBuilder().append(e.getInsuranceId())
	    						.append(e.getProductId())
	    						.append(e.getBranchCode())
	    						.append("loadtax")
	    						.append(StringUtils.join(taxFor,','))
	    						.toString();
	    				return string;
	    			}

	    		};
	    	}
	    		    	
	    	@Bean
	    	public KeyGenerator loadProRataKeyGen() {
	    		return new KeyGenerator() {
	    			@Override
	    			public Object generate(Object target, Method method, Object... params) {
	    				CalcEngine e=(CalcEngine)params[0];
	    				String periodOfInsurance=(String) params[1];
	    				String policyTypeId=(String) params[2];
	    				String string = new StringBuilder().append(e.getInsuranceId())
	    						.append(e.getProductId())
	    						.append(periodOfInsurance)
	    						.append(policyTypeId)
	    						.append("prorata")
	    						.toString();
	    				return string;
	    			}

	    		};
	    	}
	    	
	    	@Bean
	    	public KeyGenerator loadConstantKeyGen() {
	    		return new KeyGenerator() {
	    			@Override
	    			public Object generate(Object target, Method method, Object... params) {
	    				CalcEngine e=(CalcEngine)params[0];
	    				String string = new StringBuilder().append(e.getInsuranceId())
	    						.append(e.getProductId())
	    						.append(e.getBranchCode())
	    						.append("99999")
	    						.append("constant")
	    						.toString();
	    				return string;
	    			}

	    		};
	    	}
	    	
	    
	
	    	 @Bean
	    	 public KeyGenerator getProductIdBasedRawTable() {
	    		 return new KeyGenerator() {
	    			 @Override
	    			 public Object generate(Object target, Method method, Object... params) {
	    				 CalcEngine e=(CalcEngine)params[0]; 
	    				 String string = new StringBuilder().append(e.getInsuranceId())
	    						 .append(e.getProductId())
	    						 .append("rawtable")
	    						 .append(e.getSectionId())
	    						 .toString();
	    				 return string;
	    			 }

	    		 };
	    	 }
	    	 
	    	 @Bean
	    	 public KeyGenerator getEndtMasterData() {
	    		 return new KeyGenerator() {
	    			 @Override
	    			 public Object generate(Object target, Method method, Object... params) {
	    				 //CalcEngine e=(CalcEngine)params[0]; 
	    				 String string = new StringBuilder().append(params[0])
	    						 .append(params[1])
	    						 .append(params[2])
	    						 .append("EndtTable")
	    						 .append(DD_MM_YYYY.format(new Date()))
	    						 .toString();
	    				 return string;
	    			 }

	    		 };
	    	 }
	    	 @Bean
		    	public KeyGenerator getCachedRatingFieldsKeyGen() {
		    		return new KeyGenerator() {
		    			@Override
		    			public Object generate(Object target, Method method, Object... params) {
		    				CalcEngine e=(CalcEngine)params[0];
		    				RatingInfo r=(RatingInfo)params[1];
		    				String string = new StringBuilder().append(e.getInsuranceId())
		    						.append(e.getProductId())
		    						.append(e.getBranchCode())
		    						//.append(r.getRatingFieldId())
		    						.append("RatingFields")
		    						.append(e.getSectionId())
		    						.toString();
		    				return string;
		    			}

		    		};
		    	}
		    	
	    	 @Bean
		    	public KeyGenerator loadfactorOnlyqueryKeyGen() {
		    		return new KeyGenerator() {
		    			@Override
		    			public Object generate(Object target, Method method, Object... params) {
		    				CalcEngine e=(CalcEngine)params[0];
		    				 

		    				String r=(String)params[1];
		    				String r1=(String)params[2];
		    				String r2=(String)params[3];
		    				String string = new StringBuilder().append(e.getInsuranceId())
		    						.append(e.getProductId())
		    						.append(e.getBranchCode())
		    						.append(e.getSectionId())
		    						.append(e.getAgencyCode())
		    						.append(r)
		    						.append(r1)
		    						.append(r2)
		    						.append(e.getCdRefNo())
		    						.append(e.getVdRefNo())
		    						.append(e.getMsrefno())
		    						.append(e.getDdRefno())
		    						.append(DD_MM_YYYY.format(new Date()))
		    						.toString();
		    				return string;
		    			}

		    		};
		    	}
	    	 @Bean
		    	public KeyGenerator countfactorOnlyqueryKeyGen() {
		    		return new KeyGenerator() {
		    			@Override
		    			public Object generate(Object target, Method method, Object... params) {
		    				CalcEngine e=(CalcEngine)params[0];
		    				 

		    				String r=(String)params[1];
		    				String r1=(String)params[2];
		    				String r2=(String)params[3];
		    				String string = new StringBuilder().append(e.getInsuranceId())
		    						.append(e.getProductId())
		    						.append(e.getBranchCode())
		    						.append(e.getSectionId())
		    						.append(e.getAgencyCode())
		    						.append(r)
		    						.append(r1)
		    						.append(r2)
		    						.append(e.getCdRefNo())
		    						.append(e.getVdRefNo())
		    						.append(e.getMsrefno())
		    						.append(e.getDdRefno())
		    						.append(DD_MM_YYYY.format(new Date()))
		    						.toString();
		    				return string;
		    			}

		    		};
		    	}
	    	 	@Bean
		    	public KeyGenerator currencyDecimalFormatKeyGen() {

		    		return new KeyGenerator() {
		    			@Override
		    			public Object generate(Object target, Method method, Object... params) {
		    				String e=(String)params[0];
		    				String r=(String)params[1];
		    				
		    				String string = new StringBuilder().append(e)
		    						.append(r)		    						
		    						.append(DD_MM_YYYY.format(new Date()))
		    						.toString();
		    				return string;
		    			}

		    		};
		    	
	    	 	}
	    	 	
	    	 	@Bean
		    	public KeyGenerator fetchAlipaRatingKeyGen() {

		    		return new KeyGenerator() {
		    			@Override
		    			public Object generate(Object target, Method method, Object... params) {
		    				String e=(String)params[0];
		    				//String r=(String)params[1];
		    				
		    				String string = new StringBuilder().append(e)
		    						.append(e)		    						
		    					//	.append(DD_MM_YYYY.format(new Date()))
		    						.toString();
		    				return string;
		    			}

		    		};
		    	
	    	 	}
	    	 	@Bean
		    	public KeyGenerator collectCommissionDetailsKeyGen() {

		    		return new KeyGenerator() {
		    			@Override
		    			public Object generate(Object target, Method method, Object... params) {
		    				String e=(String)params[0];
		    				//String r=(String)params[1];
		    				
		    				String string = new StringBuilder().append(e)
		    						.append(e)		    						
		    					//	.append(DD_MM_YYYY.format(new Date()))
		    						.toString();
		    				return string;
		    			}

		    		};
		    	
	    	 	}
	    	 	@Bean
		    	public KeyGenerator collectProductsFromLoginIdKeyGen() {

		    		return new KeyGenerator() {
		    			@Override
		    			public Object generate(Object target, Method method, Object... params) {
		    				String e=(String)params[0];
		    				//String r=(String)params[1];
		    				
		    				String string = new StringBuilder().append(e)
		    						.append(e)		    						
		    					//	.append(DD_MM_YYYY.format(new Date()))
		    						.toString();
		    				return string;
		    			}

		    		};
		    	
	    	 	}
	    	 	
	    	 	@Bean
		    	public KeyGenerator collectSectionMasterKeyGen() {

		    		return new KeyGenerator() {
		    			@Override
		    			public Object generate(Object target, Method method, Object... params) {
		    				String e=(String)params[0];
		    				String r=(String)params[1];
		    				String f=(String)params[2];
		    				
		    				String string = new StringBuilder().append(e)
		    						.append(e)		    						
		    						.append(r)
		    						.append(f)
		    						.append(DD_MM_YYYY.format(new Date()))
		    						.toString();
		    				return string;
		    			}

		    		};
		    	
	    	 	}
	    	 	
	    	 	@Bean
		    	public KeyGenerator collectBranchMasterKeyGen() {

		    		return new KeyGenerator() {
		    			@Override
		    			public Object generate(Object target, Method method, Object... params) {
		    				String e=(String)params[0];
		    				String r=(String)params[1];
		    				//String f=(String)params[2];
		    				
		    				String string = new StringBuilder().append(e)
		    						.append(e)		    						
		    						.append(r)
		    					//	.append(f)
		    						.append(DD_MM_YYYY.format(new Date()))
		    						.toString();
		    				return string;
		    			}

		    		};
		    	
	    	 	}
	    	 	
	    	 	@Bean
		    	public KeyGenerator excludedTaxKeyGen() {
		    		return new KeyGenerator() {
		    			@Override
		    			public Object generate(Object target, Method method, Object... params) {
		    				CalcEngine e=(CalcEngine)params[0];
		    				List<String> taxFor=(List<String>) params[1];
		    				String string = new StringBuilder().append(e.getInsuranceId())
		    						.append(e.getProductId())
		    						.append(e.getBranchCode())
		    						.append(e.getSectionId())
		    						.append("excludedTax")
		    						.append(StringUtils.join(taxFor,','))
		    						.toString();
		    				return string;
		    			}

		    		};
		    	}		            	 	
	    	 	
	    	 	@Bean
		    	public KeyGenerator FlowFieldDataKeyGen() {
		    		return new KeyGenerator() {
		    			@Override
		    			public Object generate(Object target, Method method, Object... params) {
		    				WorkEngine e=(WorkEngine)params[0];
		    				String string = new StringBuilder().append(e.getCompanyId())
		    						.append(e.getProductId())
		    						.append(e.getIntegType()) 
		    						.append("FLOWFIELD")
		    						.toString();
		    				return string;
		    			}

		    		};
		    	}
	    	 	@Bean
			    public KeyGenerator DistinctQueryIdKeyGen() {
			    		return new KeyGenerator() {
			    			@Override
			    			public Object generate(Object target, Method method, Object... params) {
			    				BigDecimal e=(BigDecimal)params[0];
			    				String string = new StringBuilder().append(e.toPlainString()) 
			    						.append("DYNQUERY")
			    						.toString();
			    				return string;
			    			}

			    		};
			    	}
	    	 	
	    	 	
	    	 	@Bean
			    public KeyGenerator AzentoURLKeyGen() {
			    		return new KeyGenerator() {
			    			@Override
			    			public Object generate(Object target, Method method, Object... params) {
			    				WorkEngine e=(WorkEngine)params[0];
			    				String string = new StringBuilder().append(e.getCompanyId())
			    						.append(e.getProductId()) 
			    						.append("AUTH")
			    						.toString();
			    				return string;
			    			}

			    		};
			    	}
	    	 	
	    	 	@Bean
			    public KeyGenerator AzentoTokenKeyGen() {
			    		return new KeyGenerator() {
			    			@Override
			    			public Object generate(Object target, Method method, Object... params) {
			    				WorkEngine e=(WorkEngine)params[0];
			    				String string = new StringBuilder().append(e.getCompanyId())
			    						.append(e.getProductId()) 
			    						.append("TOKEN")
			    						.toString();
			    				return string;
			    			}

			    		};
			    	}	    	 	

}
