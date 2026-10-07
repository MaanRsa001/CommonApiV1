package com.maan.eway.thread;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.ticket.CustomerTrackerReq;
import com.maan.eway.ticket.TicketCreateReq;

public interface TicketCreateService {

	CommonRes createTicket(TicketCreateReq req);

	CommonRes createDropOffTicket(TicketCreateReq req);

	CommonRes customerJobTracking(CustomerTrackerReq req);

}
