package com.maan.eway.ui.service;

import com.maan.eway.ui.request.GroupSetup;
import com.maan.eway.ui.request.Ui;

public interface UIService {

	GroupSetup buildUIFromTable(Ui request);

}
