package com.maan.eway.bean;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.maan.eway.res.calc.Cover;
import com.maan.eway.res.calc.Tax;

import jakarta.persistence.Tuple;

public class TaxRemover implements Consumer<Cover> {

	private List<Tuple> result;
	private List<Tuple> excludedTaxesEndt;
	public TaxRemover(List<Tuple> result, List<Tuple> excludedTaxesEndt) {
		super();
		this.result = result;
		this.excludedTaxesEndt=excludedTaxesEndt;
	}

	@Override
	public void accept(Cover t) {
		 
		List<Tuple> collect = result.stream().filter(r-> r.get("coverId").toString().equals(t.getCoverId())).collect(Collectors.toList());
		if(!collect.isEmpty() && t.getTaxes()!=null && !t.getTaxes().isEmpty()) {
			for(Tuple tup: collect) {
				List<Tax> totalTax = t.getTaxes().stream().collect(Collectors.toList());
				totalTax.removeIf(e-> tup.get("taxId").toString().equals(e.getTaxId()));
				t.setTaxes(totalTax);
			}
		}
		
		if(excludedTaxesEndt!=null) {
			List<Tuple> collectEndt = excludedTaxesEndt.stream().filter(r-> r.get("coverId").toString().equals(t.getCoverId())).collect(Collectors.toList());
			if(!excludedTaxesEndt.isEmpty() && t.getEndorsements()!=null && !t.getEndorsements().isEmpty() && !t.getEndorsements().get(0).getTaxes().isEmpty()) {
				for(Tuple tup: collectEndt) {
					t.getEndorsements().get(0).getTaxes().removeIf(e-> tup.get("taxId").toString().equals(e.getTaxId()));
				}
			}
		}
	}

}
