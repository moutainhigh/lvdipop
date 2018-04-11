package com.enation.app.shop.front.tag.member;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.member.model.MemberAddress;
import com.enation.app.shop.core.member.service.IMemberAddressManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 会员地址
 * @author xulipeng
 * whj 0819 09：48修改如下：
 * 如果无默认地址，则返回一个String型 “0”。
 */

@Component
public class DefaultConsigneeTag extends BaseFreeMarkerTag{

	@Autowired
	private IMemberAddressManager memberAddressManager;
	
	@SuppressWarnings({ "rawtypes" })
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		Member member = UserConext.getCurrentMember();
		if(member == null){
			throw new TemplateModelException("未登录，不能使用此标签");
		}
		Integer memberid = member.getMember_id();
		MemberAddress address = this.memberAddressManager.getMemberDefault(memberid);
	 
		return address;
	}

}
