package com.maan.eway.workflow.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.maan.eway.workflow.dto.JsonField;

public class JsonModules implements Consumer<JsonField> {

	private List<JsonField> data;
	public JsonModules(List<JsonField> data) {
		this.data=data;
	}

	@Override
	public void accept(JsonField t) {
		try {
			
			
			
			/*if("Yes".equals(t.getIsHeader()) && t.getChildField()==null) {
				List<JsonField> child=new ArrayList<JsonField>(2);
				t.setChildField(child);
			}*/
			
			if("Yes".equals(t.getIsHeader())/*&& t.getChildField()==null*/) {
				List<JsonField> child=new ArrayList<JsonField>(0);
				t.setChildField(child);
			}
			List<JsonField> child = data.stream().filter(tx->(tx.getHeaderKeyid().compareTo(t.getKeyId())==0)).collect(Collectors.toList());
			/*if(child.size()>0)
				data.removeAll(child);*/
			t.getChildField().addAll(child);
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	

}
