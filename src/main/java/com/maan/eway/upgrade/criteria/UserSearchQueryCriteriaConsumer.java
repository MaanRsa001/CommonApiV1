package com.maan.eway.upgrade.criteria;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.ParameterExpression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class UserSearchQueryCriteriaConsumer implements Consumer<SearchCriteria>{

    private Predicate predicate;
    private CriteriaBuilder builder;
    private Root r;
    private Map<String,Object> parames;
    
    public UserSearchQueryCriteriaConsumer(Predicate predicate, CriteriaBuilder builder, Root r) {
        super();
        this.predicate = predicate;
        this.builder = builder;
        this.r= r;
        parames=new HashMap<String, Object>();
    }

    @Override
    public void accept(SearchCriteria param) {

        if (param.getOperation().equalsIgnoreCase(">")) {
            predicate = builder.and(predicate, builder.greaterThanOrEqualTo(r.get(param.getKey()), param.getValue().toString()));
        } else if (param.getOperation().equalsIgnoreCase("<")) {
            predicate = builder.and(predicate, builder.lessThanOrEqualTo(r.get(param.getKey()), param.getValue().toString()));
        } else if (param.getOperation().equalsIgnoreCase("%")) {
        	 predicate = builder.and(predicate, builder.like(builder.upper(r.get(param.getKey())),"%"+param.getValue().toString().toUpperCase()+"%"));
        }else if (param.getOperation().equalsIgnoreCase(":")) {
        	 if (param.getKey().indexOf(".")==-1 &&  (r.get(param.getKey()).getJavaType() == String.class 
        			 || r.get(param.getKey()).getJavaType()==Integer.class 
        			 || r.get(param.getKey()).getJavaType()== Long.class)) {
        		 if(param.getValues()!=null && param.getValues().size()>0) {
        			 predicate = builder.and(predicate,r.get(param.getKey()).in(param.getValues()));
            	 }else if(r.get(param.getKey()).getJavaType() == String.class)  {     		 
            		 predicate = builder.and(predicate, builder.equal(/*builder.upper()*/r.get(param.getKey()),param.getValue().toString()/*.toUpperCase()*/));
            	 }else {
            		 predicate = builder.and(predicate, builder.equal(r.get(param.getKey()),param.getValue())); 
            	 }
        	 }else if(param.getKey().indexOf(".")!=-1 ) {
             	String[] embd = param.getKey().split("\\.");
             	if(r.get(embd[0]).get(embd[1]).getJavaType()==String.class) {
             		  predicate = builder.and(predicate, builder.like( r.get(embd[0]).get(embd[1]), "%" + param.getValue() + "%"));
             	}else {
             		predicate = builder.and(predicate, builder.equal( r.get(embd[0]).get(embd[1]), param.getValue()));
             	}
            } else {
                predicate = builder.and(predicate, builder.equal(r.get(param.getKey()), param.getValue()));
            }
        	 
        	 
        	 
        	  
        }else if(param.getOperation().equalsIgnoreCase("~")) {
        	//Expression<Object> exp=()param.getValue();
        	Class class1 = r.get(param.getKey()).getJavaType();
        	 ParameterExpression parameter = builder.parameter(class1,"paramer"+param.getKey());
        	 parames.put("paramer"+param.getKey(), param.getValue());
        	//parameter.in(param.getValue().toString());
        	//parameter.as(param.getValue());        	 
        	predicate =builder.and(predicate,builder.between(parameter,  r.get(param.getKey()), r.get(param.getKey2())));
        	//createquery.setParameter(estAmt, req.getEstimateAmount());

        	//predicate =builder.and(predicate,builder.between(parameter, String.valueOf(100), String.valueOf(100000)));
        	//builder.and(predicate,builder.between(parameter, r.get(param.getKey()), r.get(param.getKey2())));
        }
    }

    public Predicate getPredicate() {
        return predicate;
    }
    
    public Map<String,Object> getParameters(){
    	return parames;
    }
    
}