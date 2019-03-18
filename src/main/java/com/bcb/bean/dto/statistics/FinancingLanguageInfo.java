package com.bcb.bean.dto.statistics;

public class FinancingLanguageInfo {

	    private String financingUuid;  //项目Uuid

	    private String title;  //项目名称

	    private String tag; //项目标签

	    private String attentions;     // 注意事件

		private String financingIconUrl;     // 理财列表背景墙url

		private String financingDetailUrl;     // 理财详情背景墙url

		public FinancingLanguageInfo() {}

		public FinancingLanguageInfo(String financingUuid, String title, String tag, String attentions,
				String financingIconUrl, String financingDetailUrl) {
			this.financingUuid = financingUuid;
			this.title = title;
			this.tag = tag;
			this.attentions = attentions;
			this.financingIconUrl = financingIconUrl;
			this.financingDetailUrl = financingDetailUrl;
		}

		public String getFinancingUuid() {
			return financingUuid;
		}

		public void setFinancingUuid(String financingUuid) {
			this.financingUuid = financingUuid;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getTag() {
			return tag;
		}

		public void setTag(String tag) {
			this.tag = tag;
		}

		public String getAttentions() {
			return attentions;
		}

		public void setAttentions(String attentions) {
			this.attentions = attentions;
		}

		public String getFinancingIconUrl() {
			return financingIconUrl;
		}

		public void setFinancingIconUrl(String financingIconUrl) {
			this.financingIconUrl = financingIconUrl;
		}

		public String getFinancingDetailUrl() {
			return financingDetailUrl;
		}

		public void setFinancingDetailUrl(String financingDetailUrl) {
			this.financingDetailUrl = financingDetailUrl;
		}
}
