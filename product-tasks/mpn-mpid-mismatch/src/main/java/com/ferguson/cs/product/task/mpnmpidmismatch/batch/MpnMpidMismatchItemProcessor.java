package com.ferguson.cs.product.task.mpnmpidmismatch.batch;

import org.springframework.batch.item.ItemProcessor;

import com.ferguson.cs.product.task.mpnmpidmismatch.client.PdmMdmWebServicesFeignClient;
import com.ferguson.cs.product.task.mpnmpidmismatch.model.MpnMpidProductItem;
import com.ferguson.cs.product.task.mpnmpidmismatch.model.mdm.MdmProductView;

public class MpnMpidMismatchItemProcessor implements ItemProcessor<MpnMpidProductItem, MpnMpidProductItem> {

	private final PdmMdmWebServicesFeignClient pdmMdmWebservicesFeignClient;

	public MpnMpidMismatchItemProcessor(PdmMdmWebServicesFeignClient pdmMdmWebservicesFeignClient) {
		this.pdmMdmWebservicesFeignClient = pdmMdmWebservicesFeignClient;
	}
	@Override
	public MpnMpidProductItem process(MpnMpidProductItem item) throws Exception {

		try {
			MdmProductView mdmProdView = pdmMdmWebservicesFeignClient.getMdmProductView(new Long(item.getMpid()));
			if (mdmProdView != null) {
				System.out.println("MPM match: " + item.getMpid());
				item.setMpidProductView(mdmProdView);
			}
		} catch (Exception e) {
			System.out.println("500 Error: " + item.getMpid());
			item.setMpidProductView(null);
		}
		return item;
	}

}
