package com.maan.eway.service.impl.referal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.UwQuestionsDetails;
import com.maan.eway.calculator.util.RatingFactorsUtil;
import com.maan.eway.calculator.util.UwQuestionUtils;
import com.maan.eway.repository.UwQuestionsDetailsRepository;
import com.maan.eway.req.calcengine.CalcEngine;
import com.maan.eway.req.referal.ReferralRequest;
import com.maan.eway.res.calc.UWReferrals;
import com.maan.eway.res.referal.MasterReferal;
import com.maan.eway.thread.MyTaskList;
import com.maan.eway.upgrade.criteria.CriteriaService;
import com.maan.eway.upgrade.criteria.SpecCriteria;

import jakarta.persistence.Tuple;

@Service
public class ReferalServiceImpl {
	
		@Autowired
	private CriteriaService crservice;
	
	@Autowired
	private RatingFactorsUtil rating;
	

	@Autowired
	private UwQuestionsDetailsRepository uwrepo;
	
	public List<MasterReferal> masterreferral(CalcEngine engine,String token) throws ClassNotFoundException {
			
			List<ReferralRequest> loadConstant = rating.LoadConstant(engine);
			
			List<Callable<Object>> queue = new ArrayList<Callable<Object>>();
			
			if(loadConstant!=null && loadConstant.size()>0) {
				
				 
				
				for (ReferralRequest r : loadConstant) {
					System.out.println("PrimaryKey  : "+r.getPrimaryKey());
					List<Tuple> result =null;
					String tablename =	r.getPrimaryTable();
					Class<?> tableClass = Class.forName("com.maan.eway.bean."+tablename);
					try {
						String search="requestReferenceNo:"+engine.getRequestReferenceNo()+";riskId:"+engine.getVehicleId()+";companyId:"+engine.getInsuranceId()+";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()+";";
						SpecCriteria criteria = crservice.createCriteria(tableClass, search, "companyId"); 
						System.out.print(tableClass +"=========" +search);						
						result = crservice.getResult(criteria, 0, 1);
					}catch (Exception e) {
						e.printStackTrace();
					}
					
					r.setPrimaryId(result!=null&& result.size()>0 ? ( result.get(0).get(r.getPrimaryKey())==null?"":result.get(0).get(r.getPrimaryKey()).toString() ) : "");
					
					List<Map<String, String>> mp = r.getMp();
					List<String> list=new ArrayList<String>();
					for(Map<String, String> map:mp){
						String jsonKey = map.get("JsonKey");
						String jsonColum = map.get("JsonColum");
						String jsonValue = result!=null&&result.size()>0 ? (result.get(0).get(jsonColum)==null?"":result.get(0).get(jsonColum).toString()) :"";
								
						String value="\""+jsonKey+"\":\""+jsonValue+"\"";
						list.add(value);		
						  
					}
				 
					r.setApiRequest("{"+StringUtils.join(list,',')+"}");
					r.setTokenl(token);
					
					System.out.println("Final Referral Request : " + r.getApiRequest());

					queue.add(new ThreadReferralCall(r));
					
				} 
				
				if(!queue.isEmpty()) {
				 MyTaskList taskList = new MyTaskList(queue);		
				 ForkJoinPool forkjoin = new ForkJoinPool((queue.size()>1 ? (queue.size()>10)?10:(int )(queue.size()/2) : 1)); 
		         ConcurrentLinkedQueue<Future<Object>> invoke  = (ConcurrentLinkedQueue<Future<Object>>) forkjoin.invoke(taskList) ;
		         int success=0;
		         List<MasterReferal> rlist= new ArrayList<MasterReferal>();
					for (Future<Object> callable : invoke) {
						System.out.println(callable.getClass() + "," + callable.isDone());
						if (callable.isDone()) {
							try {
								MasterReferal map = (MasterReferal) callable.get();
								if(map.getIsreferral()) // only referral will return
									rlist.add(map);							
							} catch (InterruptedException | ExecutionException e) {
								e.printStackTrace();
							}
							success++;
						}
					}
					return rlist;
				}
					
			}
			 
			return null;
		}
	
	
		public List<UWReferrals> underwriterReferral(CalcEngine engine ) {
			List<UWReferrals> referr=null;
			if(StringUtils.isNotBlank(engine.getRequestReferenceNo()) && StringUtils.isNotBlank(engine.getVehicleId())) {
		 
				List<UwQuestionsDetails> uwqs = uwrepo.findByCompanyIdAndProductIdAndRequestReferenceNoAndVehicleId(engine.getInsuranceId(),Integer.valueOf(engine.getProductId()),engine.getRequestReferenceNo(),Integer.valueOf(engine.getVehicleId()));
				if(!uwqs.isEmpty()) {
					List<UwQuestionsDetails> isreferral=uwqs.stream().filter(f-> "Y".equals(f.getIsReferral())).collect(Collectors.toList());
					UwQuestionUtils uts=new UwQuestionUtils();
					referr = isreferral.stream().map(uts).filter(d->d!=null).collect(Collectors.toList());
				}
				
			}
			return referr;
		}
		
}
