package com.ferguson.cs.product.task.mpnmpidmismatch.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import com.ferguson.cs.product.task.mpnmpidmismatch.client.PdmMdmWebServicesFeignClient;
import com.ferguson.cs.product.task.mpnmpidmismatch.model.MpnMpidProductItem;
import com.ferguson.cs.product.task.mpnmpidmismatch.model.mdm.MdmProductView;

public class MpnMpidMismatchItemProcessor implements ItemProcessor<MpnMpidProductItem, MpnMpidProductItem> {

	private static final Logger LOGGER = LoggerFactory.getLogger(MpnMpidMismatchItemProcessor.class);
	private final PdmMdmWebServicesFeignClient pdmMdmWebservicesFeignClient;

	public MpnMpidMismatchItemProcessor(PdmMdmWebServicesFeignClient pdmMdmWebservicesFeignClient) {
		this.pdmMdmWebservicesFeignClient = pdmMdmWebservicesFeignClient;
	}
	@Override
	public MpnMpidProductItem process(MpnMpidProductItem item) throws Exception {

		item.setMdmMpidMatch(false);
		item.setMdmMpnMatch(false);

		// MDM lookup by mpid
		try {
			LOGGER.debug("MpnMpidMismatchItemProcessor - calling getMdmProductView api with mpid: {}", item.getFmMpid());
			MdmProductView mdmMpidProdView = pdmMdmWebservicesFeignClient.getMdmProductView(new Long(item.getFmMpid()));

			if (mdmMpidProdView != null) {
				item.setMdmMpidMatch(true);
				populateMpidItemDetails(item,mdmMpidProdView, true);
			}
		} catch (Exception e) {
			LOGGER.error("MpnMpidMismatchItemProcessor - getMdmProductView() error: {}", e.getMessage());
		}

		// MDM lookup by mpn
		try {
			LOGGER.debug("MpnMpidMismatchItemProcessor - calling getMdmProductView api with mpn: {}", item.getVmMpn());
			MdmProductView mdmMpnProdView = pdmMdmWebservicesFeignClient.getMdmProductView(new Long(item.getVmMpn()));

			if (mdmMpnProdView != null) {
				item.setMdmMpnMatch(true);
				populateMpidItemDetails(item,mdmMpnProdView, false);
			}
		} catch (Exception e) {
			LOGGER.error("MpnMpidMismatchItemProcessor - getMdmProductView() error: {}", e.getMessage());
		}

		return item;
	}

	/*
	 * Push the MDM result for mpid into our reporting object
	 */
	private void populateMpidItemDetails(MpnMpidProductItem item, MdmProductView mpidMdmProductViewItem, boolean isMpid) {

		if (mpidMdmProductViewItem.getAttributes() != null) {
			if (isMpid) {
				item.setMdmMpidPrimaryVendorId(mpidMdmProductViewItem.getAttributes().getPrimaryVendorId());
				item.setMdmMpidDescription(mpidMdmProductViewItem.getAttributes().getLongDescription());
				item.setMdmMpidUpc(mpidMdmProductViewItem.getAttributes().getUpc());
				item.setMdmMpidSku(mpidMdmProductViewItem.getAttributes().getVendorProductCode());
				item.setMdmMpidAlternateCode(mpidMdmProductViewItem.getAttributes().getAlternateCode1());
			} else {
				item.setMdmMpnPrimaryVendorId(mpidMdmProductViewItem.getAttributes().getPrimaryVendorId());
				item.setMdmMpnDescription(mpidMdmProductViewItem.getAttributes().getLongDescription());
				item.setMdmMpnUpc(mpidMdmProductViewItem.getAttributes().getUpc());
				item.setMdmMpnSku(mpidMdmProductViewItem.getAttributes().getVendorProductCode());
				item.setMdmMpnAlternateCode(mpidMdmProductViewItem.getAttributes().getAlternateCode1());
			}
		}
	}
}
