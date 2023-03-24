package com.docusign.proserv.application.utils;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.support.DefaultBannerProvider;
import org.springframework.shell.support.util.OsUtils;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PSBannerProvider extends DefaultBannerProvider {

	public String getBanner() {
		StringBuffer buf = new StringBuffer();
		buf.append("=======================================" + OsUtils.LINE_SEPARATOR);
		buf.append("*                                     *" + OsUtils.LINE_SEPARATOR);
		buf.append("*            PS Utilities            *" + OsUtils.LINE_SEPARATOR);
		buf.append("*                                     *" + OsUtils.LINE_SEPARATOR);
		buf.append("=======================================" + OsUtils.LINE_SEPARATOR);
		buf.append("Version:" + this.getVersion());
		return buf.toString();
	}

	public String getVersion() {
		return "1.0";
	}

	public String getWelcomeMessage() {
		return "Welcome to PS Utilities! For assistance hit TAB or type 'help' and hit ENTER.";
	}

	@Override
	public String getProviderName() {
		return "PS Utilities";
	}
}