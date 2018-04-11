package com.enation.app.shop.mobile.action.order;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import net.sf.json.JSONArray;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.enation.app.b2b2c.component.bonus.service.B2b2cBonusSession;
import com.enation.app.b2b2c.component.bonus.service.IB2b2cBonusManager;
import com.enation.app.b2b2c.component.plugin.order.StoreCartPluginBundle;
import com.enation.app.b2b2c.core.goods.service.StoreCartContainer;
import com.enation.app.b2b2c.core.goods.service.StoreCartKeyEnum;
import com.enation.app.b2b2c.core.member.model.StoreMember;
import com.enation.app.b2b2c.core.member.service.IStoreMemberManager;
import com.enation.app.b2b2c.core.order.model.StoreOrder;
import com.enation.app.b2b2c.core.order.model.cart.StoreCartItem;
import com.enation.app.b2b2c.core.order.service.IStoreOrderManager;
import com.enation.app.b2b2c.core.order.service.cart.IStoreCartManager;
import com.enation.app.b2b2c.core.store.model.Store;
import com.enation.app.b2b2c.core.store.service.IStoreManager;
import com.enation.app.b2b2c.core.store.service.activity.IStoreActivityGiftManager;
import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.javashop.customized.core.service.IMemberCouponsManager;
import com.enation.app.javashop.customized.core.service.IStoreCouponsManager;
import com.enation.app.shop.component.bonus.model.MemberBonus;
import com.enation.app.shop.component.bonus.model.MemberCoupons;
import com.enation.app.shop.component.receipt.service.IReceiptManager;
import com.enation.app.shop.core.member.model.MemberAddress;
import com.enation.app.shop.core.member.service.IMemberAddressManager;
import com.enation.app.shop.core.order.model.Delivery;
import com.enation.app.shop.core.order.model.Order;
import com.enation.app.shop.core.order.model.OrderItem;
import com.enation.app.shop.core.order.model.PayCfg;
import com.enation.app.shop.core.order.model.SellBackGoodsList;
import com.enation.app.shop.core.order.model.SellBackList;
import com.enation.app.shop.core.order.model.support.CartItem;
import com.enation.app.shop.core.order.model.support.OrderPrice;
import com.enation.app.shop.core.order.plugin.cart.CartPluginBundle;
import com.enation.app.shop.core.order.service.ICartManager;
import com.enation.app.shop.core.order.service.IDlyTypeManager;
import com.enation.app.shop.core.order.service.IOrderBonusManager;
import com.enation.app.shop.core.order.service.IOrderFlowManager;
import com.enation.app.shop.core.order.service.IOrderGiftManager;
import com.enation.app.shop.core.order.service.IOrderManager;
import com.enation.app.shop.core.order.service.IOrderReportManager;
import com.enation.app.shop.core.order.service.IPaymentManager;
import com.enation.app.shop.core.order.service.ISellBackManager;
import com.enation.app.shop.core.order.service.OrderStatus;
import com.enation.app.shop.core.other.service.IActivityDetailManager;
import com.enation.app.shop.core.other.service.impl.ExpressManager;
import com.enation.app.shop.mobile.model.ApiSellBack;
import com.enation.app.shop.mobile.service.ApiOrderManager;
import com.enation.app.shop.mobile.service.CrmPointManager;
import com.enation.app.shop.mobile.service.IApiStoreAttentionManager;
import com.enation.app.shop.mobile.service.impl.ApiCartManager;
import com.enation.app.shop.mobile.utils.CommonRequest;
import com.enation.app.shop.mobile.utils.PointUtils;
import com.enation.app.shop.mobile.vo.OrderVo;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.utils.StaticResourcesUtil;
import com.enation.framework.action.JsonResult;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.util.CurrencyUtil;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.JsonResultUtil;
import com.enation.framework.util.StringUtil;

/**
 * 订单Api
 * 提供
 * @author Sylow
 * @version v1.0
 * @since v1.0
 */
@Controller
@RequestMapping("/api/mobile/order")
public class OrderMobileApiController {
	protected final Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private ApiOrderManager apiOrderManager;
	@Autowired
	private IOrderManager orderManager;
	@Autowired
	private IMemberAddressManager memberAddressManager;
	@Autowired
	private IPaymentManager paymentManager;
	@Autowired
	private ICartManager cartManager;
//	@Autowired
//	private IDlyTypeManager dlyTypeManager;
//	@Autowired
//	private IReceiptManager receiptManager;
	@Autowired
	private IOrderFlowManager orderFlowManager;
	@Autowired
	private IStoreCartManager storeCartManager;
	@Autowired
	private IStoreOrderManager storeOrderManager;
	@Autowired
	private CartPluginBundle cartPluginBundle;
	@Autowired
	private IOrderReportManager orderReportManager;
	@Autowired
    private ExpressManager expressManager;
	
//	@Autowired
//	private IOrderGiftManager orderGiftManager;
//	@Autowired
//    private IOrderBonusManager orderBonusManager;
	
	@Autowired
	private IStoreManager storeManager;
	
	private final int PAGE_SIZE = 10;
	
	@Autowired
	private ISellBackManager sellBackManager;
	 
	@Autowired
	private IStoreMemberManager storeMemberManager;
	
	@Autowired
	private StoreCartPluginBundle storeCartPluginBundle;
	
	@Autowired
	private IB2b2cBonusManager b2b2cBonusManager;
	  /**
     * 促销活动优惠详细管理接口
     */
    @Autowired
    private IActivityDetailManager activityDetailManager;
    
    /**
     * 促销活动赠品管理接口
     */
    @Autowired
    private IStoreActivityGiftManager storeActivityGiftManager;
    
    
    @Autowired
	private IStoreCouponsManager storeCouponsManager;
    
    @Autowired
	private IMemberCouponsManager memberCouponsManager;
    
    @Autowired
    private IMemberManager memberManager;
    
    @Autowired
    private ApiCartManager apiCartManager;
    
    @Autowired
    private CrmPointManager crmPointManager;		
    
    @Autowired
	IApiStoreAttentionManager apiStoreAttentionManager;
    

	@SuppressWarnings({ "rawtypes", "unchecked" })
	/**
	 * 获取支付方式
	 * @retur
	 */
	@ResponseBody
	@RequestMapping(value="/payment",produces= MediaType.APPLICATION_JSON_VALUE)
	public JsonResult payment(){
		try {
			List paymentList  = this.paymentManager.list();
			Map data = new HashMap();
			data.put("payment", paymentList);
			return JsonResultUtil.getObjectJson(data);

		} catch (RuntimeException e) {
			this.logger.error("获取支付方式出错", e);
			return JsonResultUtil.getErrorJson("获取支付方式出错[" + e.getMessage() + "]");
		}
	}
	/**
	 * 获取支付方式
	 * @return
	 */
	@RequestMapping(value="/payment-detail",produces= MediaType.APPLICATION_JSON_VALUE)
	public JsonResult paymentDetail(int id){ 
		try {
			Map data = new HashMap();
			data.put("payment", paymentManager.get(id));
			return JsonResultUtil.getObjectJson(data);
			
		} catch (RuntimeException e) {
			this.logger.error("获取支付方式出错", e);
			return JsonResultUtil.getErrorJson("获取支付方式出错[" + e.getMessage() + "]");
		}
	}

	/**
	 * 获取支付方式，适配原生app
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/payment-app",produces= MediaType.APPLICATION_JSON_VALUE)
	public JsonResult paymentApp(){
		try {
			Map data = new HashMap();
			List dbPaymentList  = this.paymentManager.list();
			List paymentList = new ArrayList();
			for(int i = 0; i < dbPaymentList.size(); i++){
				PayCfg payment = (PayCfg)dbPaymentList.get(i);
				if(payment.getType().equals("cod") || payment.getType().endsWith("MobilePlugin")){
					paymentList.add(payment);
				}
			}
			data.put("payment", paymentList);
			return JsonResultUtil.getObjectJson(data);

		} catch (RuntimeException e) {
			this.logger.error("获取支付方式出错", e);
			return JsonResultUtil.getErrorJson("获取支付方式出错[" + e.getMessage() + "]");
		}
	}


	/**
	 * 获取支付及配送方式
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/payment-shipping",produces= MediaType.APPLICATION_JSON_VALUE)
	public JsonResult paymentShipping(){
		//支付方式
		List dbPaymentList  = this.paymentManager.list();
		List paymentList = new ArrayList();
		for(int i = 0; i < dbPaymentList.size(); i++){
			PayCfg payment = (PayCfg)dbPaymentList.get(i);
			if(payment.getType().equals("cod") || payment.getType().endsWith("MobilePlugin")){
				paymentList.add(payment);
			}
		}
		Map data = new HashMap();
		data.put("payment", paymentList); 
		return JsonResultUtil.getObjectJson(data);
	}

	/**
	 * 获取购物车所有商品、商品所对应的商家、商家的配送方式等信息
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/store-cart-goods",produces= MediaType.APPLICATION_JSON_VALUE)
	public JsonResult storeCartGoods(){
		try {
			HttpServletRequest request =ThreadContextHolder.getHttpRequest();
			String sessionid  = request.getSession().getId();
			Member member = UserConext.getCurrentMember();
			if (member == null) {
				return JsonResultUtil.getErrorJson("未登录");
			}
			this.storeCartManager.countPrice("yes");
			List<Map> list = StoreCartContainer.getSelectStoreCartListFromSession();
			List<CartItem> cartList  = cartManager.listGoods(sessionid);
			//计算订单价格
			OrderPrice orderPrice  =this.cartManager.countPrice(cartList, null,null);
			//激发价格计算事件
			orderPrice  = this.cartPluginBundle.coutPrice(orderPrice);
			Map map = new HashMap();
			map.put("store_list", list);
			map.put("order_price", orderPrice);
			return JsonResultUtil.getObjectJson(map); 

		} catch(RuntimeException e) {
			this.logger.error("获取商品信息出错", e);
			return JsonResultUtil.getErrorJson("获取商品信息出错[" + e.getMessage() + "]");
		}
	}


	/**
	 * 获得订单总价
	 * @param address_id 必填 地址id
	 * @param shipping_id 必填 配送方式id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/get-total-price",produces= MediaType.APPLICATION_JSON_VALUE)
	public JsonResult getTotalPrice(){
		try {
			HttpServletRequest request =ThreadContextHolder.getHttpRequest();
			String sessionid  = request.getSession().getId();
			List<CartItem> cartList  = cartManager.listGoods(sessionid);
			//计算订单价格
			OrderPrice orderprice  =this.cartManager.countPrice(cartList, null,null);
			//激发价格计算事件
			orderprice  = cartPluginBundle.coutPrice(orderprice);
			return JsonResultUtil.getObjectJson(orderprice);
		} catch(RuntimeException e) {
			this.logger.error("获取订单总价出错", e);
			return JsonResultUtil.getErrorJson("获取订单总价出错[" + e.getMessage() + "]");
		}
	}

	/**
	 * 改变配送方式
	 * @param address_id 必填 地址id
	 * @param shipping_id 必填 配送方式id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/change-shipping-type",produces= MediaType.APPLICATION_JSON_VALUE)
	public JsonResult changeShippingType(){    	
		HttpServletRequest request =ThreadContextHolder.getHttpRequest();
		int store_id = NumberUtils.toInt(request.getParameter("store_id"),0);
		String regionid_str = request.getParameter("region_id");
		int type_id = NumberUtils.toInt(request.getParameter("type_id"), 0);
		// 由购物车列表中获取此店铺的相关信息
		Map storeData = StoreCartContainer.getStoreData(store_id);
		// 获取此店铺的购物列表
		List list = (List) storeData.get(StoreCartKeyEnum.goodslist.toString());
		// 计算此配送方式时的店铺相关价格
		OrderPrice orderPrice = this.cartManager.countPrice(list, type_id,
				regionid_str);
		// 激发计算子订单价格事件
		//		orderPrice = storeCartPluginBundle.countChildPrice(orderPrice);
		// 重新压入此店铺的订单价格和配送方式id
		storeData.put(StoreCartKeyEnum.storeprice.toString(), orderPrice);
		storeData.put(StoreCartKeyEnum.shiptypeid.toString(), type_id);
		return JsonResultUtil.getObjectJson(orderPrice, "storeprice");
	}

	/**
	 * 提交订单
	 * usepoint 是否使用积分
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/create",produces= MediaType.APPLICATION_JSON_VALUE)
	public JsonResult create(@ModelAttribute OrderVo orderVo,String usepoint) {
			boolean usePoint = false;
			if(StringUtils.isNotEmpty(usepoint)){
				usePoint = Boolean.valueOf(usepoint);
			}
		
			Integer member_id = CommonRequest.getMemberID();
	        if (member_id == null) {
	            return JsonResultUtil.getErrorJson("您没有登录或登录过期");
	        }
	        if (orderVo.getAddressId() == null || orderVo.getAddressId() <= 0) {
	            return JsonResultUtil.getErrorJson("请选择收货地址");
	        }
	        MemberAddress address = memberAddressManager.getAddress(orderVo.getAddressId());
	        if (address == null || !address.getMember_id().equals(member_id)) {
	            return JsonResultUtil.getErrorJson("收货地址不存在");
	        }
	        try {
	            // 获取用户选择的支付方式ID
	            // 通过支付ID获取支付方式类型
	        	PayCfg  payCfg = this.paymentManager.list().get(0);//默认选择
//	            String payType = this.paymentManager.get(orderVo.getPaymentId()).getType();
	            //创建订单
	            Order order = new Order();
	            order.setShipping_id(0);  // 主订单没有配送方式
	            order.setPayment_id(payCfg.getId());// 支付方式
	            order.setShip_provinceid(address.getProvince_id());
	            order.setShip_cityid(address.getCity_id());
	            order.setShip_regionid(address.getRegion_id());
	            order.setShip_addr(address.getAddr());
	            order.setShip_mobile(address.getMobile());
	            order.setShip_tel(address.getTel());
	            order.setShip_zip(address.getZip());
	            String shiparea = address.getProvince() + " " +address.getCity() ;
	            if(address.getRegion()!=null){
	            	shiparea = shiparea + " " + address.getRegion();
	            }
	            order.setShipping_area(shiparea);
	            order.setShip_name(address.getName());
	            order.setRegionid(address.getRegion_id());
	            order.setMemberAddress(address);
	            order.setShip_day(orderVo.getShippingTime());
	            order.setShip_time("");
	            order.setRemark(orderVo.getRemark());
	            order.setAddress_id(address.getAddr_id());
	            
	            try {
	            	 order = apiOrderManager.createOrder(order, member_id,usePoint);
				} catch (Exception e) {
					this.logger.error("提交订单出错", e);
					return JsonResultUtil.getErrorJson("[" + e.getMessage() + "]");
				}
	            return JsonResultUtil.getObjectJson(order);
	        } catch (RuntimeException e) {
	        	this.logger.error("提交订单出错", e);
				return JsonResultUtil.getErrorJson("[" + e.getMessage() + "]");
	        }
	}

	/**
	 * 初始化订单
	 * @return
	 */ 
	private Order innerCreateOrder() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		Integer shippingId = 0; // 主订单没有配送方式
		Integer paymentId = StringUtil.toInt(request.getParameter("paymentId"),
				0);
		if (paymentId == 0)
			throw new RuntimeException("支付方式不能为空");
		Order order = new Order();
		order.setShipping_id(shippingId); // 配送方式
		order.setPayment_id(paymentId);// 支付方式
		// 用户选中的地址
//		MemberAddress address = memberAddressManager.getAddress(StringUtil.toInt(request.getParameter("addressId")));
		MemberAddress address = StoreCartContainer.getUserSelectedAddress();
		if (address == null) {
			throw new RuntimeException("收货地址不能为空");
		}
		order.setShip_provinceid(address.getProvince_id());
		order.setShip_cityid(address.getCity_id());
		order.setShip_regionid(address.getRegion_id());
		order.setShip_addr(address.getAddr());
		order.setShip_mobile(address.getMobile());
		order.setShip_tel(address.getTel());
		order.setShip_zip(address.getZip());
		order.setShipping_area(address.getProvince() + "-" + address.getCity()
		+ "-" + address.getRegion());
		order.setShip_name(address.getName());
		order.setRegionid(address.getRegion_id());
		order.setMemberAddress(address);
		order.setShip_day(request.getParameter("shipDay"));
		order.setShip_time(request.getParameter("shipTime"));
		order.setRemark(request.getParameter("remark"));
		order.setAddress_id(address.getAddr_id());
		String sessionid = request.getSession().getId();
		order = this.storeOrderManager.createOrder(order, sessionid);
		return order;
	}
	
	
	/**
	 * 订单列表
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/list",produces= MediaType.APPLICATION_JSON_VALUE)
	public JsonResult listOrders(){
		try {
			 Integer memberId = CommonRequest.getMemberID();
			if (memberId == null) {
				return JsonResultUtil.getErrorJson("您没有登录或登录过期！");
			}
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			String page = request.getParameter("page");
			String pageSize = request.getParameter("pageSize");
			page = (page == null || page.equals("")) ? "1" : page;
			if(StringUtils.isEmpty(pageSize)){
				pageSize = "10";
			}
			String status = request.getParameter("status");			
			//查询父订单
			Page ordersPage = apiOrderManager.pagePayOrders(
					Integer.valueOf(page),Integer.valueOf(pageSize), status,memberId);
			ordersPage.setCurrentPageNo(Long.valueOf(page));
			return JsonResultUtil.getObjectJson(ordersPage);
		} catch (RuntimeException e) {
			e.printStackTrace();
			this.logger.error("获取订单列表出错", e);
			return JsonResultUtil.getErrorJson(e.getMessage());
		}
	}
	

	@ResponseBody
	@RequestMapping(value="/listapp")
	public JsonResult listapp(){
		Member member =UserConext.getCurrentMember();

		if (member == null) {
			return JsonResultUtil.getErrorJson("您没有登录或登录过期！");
		}
		HttpServletRequest request=ThreadContextHolder.getHttpRequest();
		int page  = NumberUtils.toInt(request.getParameter("page"), 1);

		Page ordersPage  = storeOrderManager.pageChildOrders(
				Integer.valueOf(page), 10, "", "");
		JsonResult json = new JsonResult(); 
		json.setResult(1);
		json.setData(JSONArray.fromObject(ordersPage.getResult()));
		return json;
	}

//	/**
//	 * 订单详细列表，父子订单
//	 * @return
//	 */
//	@ResponseBody
//	@RequestMapping(value="/detail",produces= MediaType.APPLICATION_JSON_VALUE)
//	public JsonResult detail(Integer orderid){
//		Integer member_id = CommonRequest.getMemberID();
//	        if (member_id == null) {
//	        	return JsonResultUtil.getErrorJson("您没有登录或登录过期！");
//	        }
//	        Order order = orderManager.get(orderid);
//	        if (order == null) {
//	        	return JsonResultUtil.getErrorJson("此订单不存在！");
//	        }
//	        if (!order.getMember_id().equals(member_id)) {
//	        	return JsonResultUtil.getErrorJson("您没有权限进行此项操作！");
//	        }
//	        order.setItems_json("");
//	        List<OrderItem> orderItemList = apiOrderManager.listGoodsItems(orderid);
//	        for (OrderItem orderItem : orderItemList) {
//	            orderItem.setImage(StaticResourcesUtil.convertToUrl(orderItem.getImage()));
//	        }
//	        order.setOrderItemList(orderItemList);
//	        //赠品及优惠券
////	        if (order.getGift_id() != null && order.getGift_id().intValue() > 0) {
////	            OrderGift orderGift = orderGiftManager.getOrderGift(order.getGift_id(), order.getOrder_id());
////	            if(orderGift != null) {
////	                orderGift.setGift_img(StaticResourcesUtil.convertToUrl(orderGift.getGift_img()));
////	                order.getFields().put("gift", orderGift);
////	            }
////	        }
////	        if (order.getBonus_id() != null && order.getBonus_id().intValue() > 0) {
////	            order.getFields().put("bonus", orderBonusManager.getOrderBonus(order.getBonus_id(), order.getOrder_id()));
////	        }
//	        Map dataMap = new HashMap();
//	        dataMap.put("order", order);
//	        dataMap.put("receipt", receiptManager.getByOrderid(order.getOrder_id()));
//	        return JsonResultUtil.getObjectJson(dataMap);
//	}
	
	
	/**
	 * 订单详细列表，父子订单
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/detail",produces= MediaType.APPLICATION_JSON_VALUE)
	public JsonResult detail(Integer orderid){
		Integer member_id = CommonRequest.getMemberID();
	        if (member_id == null) {
	        	return JsonResultUtil.getErrorJson("您没有登录或登录过期！");
	        }
	        StoreOrder order = storeOrderManager.get(orderid);
	        if (order == null) {
	        	return JsonResultUtil.getErrorJson("此订单不存在！");
	        }
	        if (!order.getMember_id().equals(member_id)) {
	        	return JsonResultUtil.getErrorJson("您没有权限进行此项操作！");
	        }
	        Map dataMap = apiOrderManager.orderDetails(order);
	        return JsonResultUtil.getObjectJson(dataMap);
	}
	
	

	/**
	 * 申请取消订单
	 * @param sn:订单序列号.String型，必填项
	 * @return 返回json串
	 * result  为1表示添加成功0表示失败 ，int型
	 * message 为提示信息
	 */
	@ResponseBody
	@RequestMapping(value="/cancel",produces= MediaType.APPLICATION_JSON_VALUE)
	public JsonResult cancel(Integer orderid, String reason) {
		Integer memberId = CommonRequest.getMemberID();
		String ticket = CommonRequest.getReqTicket();
		if (memberId == null) {
			return JsonResultUtil.getErrorJson("您没有登录或登录过期！");
		}
		StoreOrder order = storeOrderManager.get(orderid);
		if (order == null || !order.getMember_id().equals(memberId)) {
			return JsonResultUtil.getErrorJson("此订单不存在！");
		}
		if (order.getStatus().intValue() != OrderStatus.ORDER_NOT_PAY
				&& order.getStatus().intValue() != OrderStatus.ORDER_PAY
				&& order.getStatus().intValue() != OrderStatus.ORDER_CONFIRM) {
			return JsonResultUtil.getErrorJson("此订单不能取消，请联系客服人员！");
		}
		
		// 不重复提交取消申请
		if (order.getIs_cancel().intValue() == 0) {
			this.orderManager.addCancelApplication(orderid, reason);// 申请取消订单
			if (order.getStatus().intValue() == OrderStatus.ORDER_CONFIRM) {
				this.orderManager.authCancelApplication(orderid, 1);// 执行取消订单事件
				if(order.getConsumepoint() > 0){
					crmPointManager.addMemberPoint(ticket, order.getConsumepoint(), order.getSn());
				}
			}
			
			if (order.getParent_id() == null) {// 如果是父订单
				List<StoreOrder> cOrderList = storeOrderManager.storeOrderList(order.getOrder_id());
				for (StoreOrder childOrder : cOrderList) {
					this.orderManager.addCancelApplication(childOrder.getOrder_id(), reason);
					if (order.getStatus().intValue() == OrderStatus.ORDER_CONFIRM) {
						this.orderManager.authCancelApplication(childOrder.getOrder_id(), 1);
					}
				}
			}
		}
		return JsonResultUtil.getSuccessJson("取消订单成功");
	}

	
	/**
	 * 确认收货
	 * @param orderId:订单id.String型，必填项
	 *
	 * @return 返回json串
	 * result  为1表示添加成功0表示失败 ，int型
	 * message 为提示信息
	 */
	@ResponseBody
	@RequestMapping(value="/rog-confirm",produces= MediaType.APPLICATION_JSON_VALUE)
	public JsonResult rogConfirm(Integer orderid) {
		Integer memberId = CommonRequest.getMemberID();
		Member member = memberManager.get(memberId);
		if (memberId == null) {
			return JsonResultUtil.getErrorJson("您没有登录或登录过期！");
		}
		Order order = orderManager.get(orderid);
		if (order == null || !order.getMember_id().equals(member.getMember_id())) {
			return JsonResultUtil.getErrorJson("此订单不存在！");
		}
		if (order.getStatus().intValue() != OrderStatus.ORDER_SHIP) {
			return JsonResultUtil.getErrorJson("此订单不能确认收货，请联系客服人员！");
		}
		this.orderFlowManager.rogConfirm(orderid, member.getMember_id(), member.getUname(), member.getUname(),
				DateUtil.getDateline());
		return JsonResultUtil.getSuccessJson("确认收货成功");
	}
	
	
	  /**
		* 查询订单物流信息
		* @return
		*/
	@ResponseBody
	@RequestMapping(value = "/shipInfo", produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult shipInfo(int order_id) {
		Delivery delivery = null;
		List<Delivery> deliveryList = orderReportManager.getDeliveryList(order_id);
		for (Delivery d : deliveryList) {
			if (d.getType().intValue() == 1 && !StringUtils.isEmpty(d.getLogi_no())) {
				delivery = d;
				break;
			}
		}
		if (delivery == null) {
			return JsonResultUtil.getErrorJson("此订单暂时没有物流信息！");
		}
		List expressList = new ArrayList();
		Map expressMap = this.expressManager.getDefPlatform(delivery.getLogi_code(), delivery.getLogi_no());
		if (expressMap != null && expressMap.containsKey("message")
				&& expressMap.get("message").toString().equals("ok")) {
			expressList = (List) expressMap.get("data");
		}

		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put("delivery", delivery);
		dataMap.put("express", expressList);
		return JsonResultUtil.getObjectJson(dataMap);
	}
	
	
//	   /**
//     * 退货申请
//     *
//     * @param order_id       订单id
//     * @param goods_num      商品退货申请数量
//     * @param item_id        【必填】订单项id
//     * @param refund_way     【必填】退款方式
//     * @param remark         【必填】 描述
//     * @param return_account 【必填】退款账号
//     * @param reason         退货原因
//     * @param ship_status    是否收到货
//     * @param apply_alltotal 退款金额
//     * @return
//     * 
//     * 去掉参数   Integer[] goods_num, Integer[] item_id,   默认退掉所有的货 
//     */
//	//TODO
//    @ResponseBody
//    @RequestMapping(value = "/returned", produces = MediaType.APPLICATION_JSON_VALUE)
//    public JsonResult apply(int order_id,
//                            String refund_way, String remark, String return_account,
//                            String reason, Integer ship_status, double apply_alltotal) {
//        try {
//            if (StringUtil.isEmpty(refund_way)) {
//                return JsonResultUtil.getErrorJson("退款方式不能为空");
//            }
//            if (StringUtil.isEmpty(return_account)) {
//                return JsonResultUtil.getErrorJson("退款账号不能为空");
//            }
//            Integer member_id = CommonRequest.getMemberID();
//            Member member = memberManager.get(member_id);
//            if (member == null) {
//                return JsonResultUtil.getErrorJson("请您在登录后再申请退货！");
//            }
//            Order order = orderManager.get(order_id);
//            if (order == null
//                    || !order.getMember_id().equals(member.getMember_id())) {
//                return JsonResultUtil.getErrorJson("此订单不存在！");
//            }
//            List<OrderItem> orderItems = new ArrayList<OrderItem>();
//            orderItems = apiOrderManager.listGoodsItems(order_id);
//            SellBackList sellBack = new SellBackList();
//            sellBack.setMember_id(member.getMember_id());
//            sellBack.setSndto(member.getName());
//            sellBack.setTradeno(com.enation.framework.util.DateUtil.toString(
//                    DateUtil.getDateline(), "yyMMddhhmmss"));// 退货单号
//            sellBack.setOrderid(order_id);
//            sellBack.setRegoperator("会员[" + member.getUname() + "]");
//            sellBack.setTradestatus(0);
//            sellBack.setRegtime(DateUtil.getDateline());
//            sellBack.setRemark(remark);
//            sellBack.setType(2);
//            sellBack.setRefund_way(refund_way);
//            sellBack.setReturn_account(return_account);
//            sellBack.setReason(reason);
//            sellBack.setShip_status(ship_status.toString());
//            sellBack.setApply_alltotal(apply_alltotal);
//            /**
//             * 循环页面中选中的商品，形成退货明细:goodsList
//             */
//            List goodsList = new ArrayList();
//            for (OrderItem item : orderItems) {
//            	SellBackGoodsList sellBackGoods = new SellBackGoodsList();
//	              sellBackGoods.setPrice(item.getPrice());
//	              sellBackGoods.setReturn_num(item.getNum());
//	              sellBackGoods.setShip_num(item.getShip_num());
//	              sellBackGoods.setGoods_id(item.getGoods_id());
//	              sellBackGoods.setGoods_remark(remark);
//	              sellBackGoods.setProduct_id(item.getProduct_id());
//	              sellBackGoods.setItem_id(item.getItem_id());
//	              goodsList.add(sellBackGoods);
//			}
//            this.sellBackManager.addSellBack(sellBack, goodsList);
//            return JsonResultUtil.getSuccessJson("申请退货成功，请等待客服人员审核！");
//        } catch (RuntimeException e) {
//            return JsonResultUtil.getErrorJson("申请退货失败，请您重试！");
//        }
//    }
	
	
	  /**
     * 退货申请
     *
     * @param order_id       订单id
     * @param goods_num      商品退货申请数量
     * @param item_id        【必填】订单项id
     * @param refund_way     【必填】退款方式
     * @param remark         【必填】 描述
     * @param return_account 【必填】退款账号
     * @param reason         退货原因
     * @param ship_status    是否收到货
     * @param apply_alltotal 退款金额
     * @return
     * 
     * 去掉参数   Integer[] goods_num, Integer[] item_id,   默认退掉所有的货 
     */
	//TODO
    @ResponseBody
    @RequestMapping(value = "/returned", produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult apply(int order_id, String remark, String reason) {
		try {
			Integer member_id = CommonRequest.getMemberID();
			Member member = memberManager.get(member_id);
			if (member == null) {
				return JsonResultUtil.getErrorJson("请您在登录后再申请退货！");
			}
			Order order = orderManager.get(order_id);
			if (order == null || !order.getMember_id().equals(member.getMember_id())) {
				return JsonResultUtil.getErrorJson("此订单不存在！");
			}
			List<OrderItem> orderItems = new ArrayList<OrderItem>();
			orderItems = apiOrderManager.listGoodsItems(order_id);
			SellBackList sellBack = new SellBackList();
			sellBack.setMember_id(member.getMember_id());
			sellBack.setSndto(member.getName());
			sellBack.setTradeno(com.enation.framework.util.DateUtil.toString(DateUtil.getDateline(), "yyMMddhhmmss"));// 退货单号
			sellBack.setOrderid(order_id);
			sellBack.setRegoperator("会员[" + member.getUname() + "]");
			sellBack.setTradestatus(0);
			sellBack.setRegtime(DateUtil.getDateline());
			sellBack.setRemark(remark);
			sellBack.setType(2);
			sellBack.setRefund_way(order.getPayment_name());
			// sellBack.setReturn_account();
			sellBack.setReason(reason);
			sellBack.setShip_status("1");// 默认为已收货
			sellBack.setApply_alltotal(order.getPaymoney());
			/**
			 * 循环页面中选中的商品，形成退货明细:goodsList
			 */
			List goodsList = new ArrayList();
			for (OrderItem item : orderItems) {
				SellBackGoodsList sellBackGoods = new SellBackGoodsList();
				sellBackGoods.setPrice(item.getPrice());
				sellBackGoods.setReturn_num(item.getNum());
				sellBackGoods.setShip_num(item.getShip_num());
				sellBackGoods.setGoods_id(item.getGoods_id());
				sellBackGoods.setGoods_remark(remark);
				sellBackGoods.setProduct_id(item.getProduct_id());
				sellBackGoods.setItem_id(item.getItem_id());
				goodsList.add(sellBackGoods);
			}
			this.sellBackManager.addSellBack(sellBack, goodsList);
			return JsonResultUtil.getSuccessJson("申请退货成功，请等待客服人员审核！");
		} catch (RuntimeException e) {
			return JsonResultUtil.getErrorJson("申请退货失败，请您重试！");
		}
	}
	
	
	
    
    /**
     * 退换货详情
     *
     * @param id
     * @param orderid
     * @return
     */
    @SuppressWarnings("unchecked")
	@ResponseBody
    @RequestMapping(value = "/sell-back", produces = MediaType.APPLICATION_JSON_VALUE)
    public JsonResult sellBack(Integer id, Integer orderid) {	
        SellBackList SellBack = null;
        if (id != null && id.intValue() > 0) {
            SellBack = sellBackManager.get(id);
        }
        if (orderid != null && orderid.intValue() > 0) {
            SellBack = apiOrderManager.getSellBack(orderid);
        }
        if (SellBack == null) {
            return JsonResultUtil.getErrorJson("此售后信息不存在！");
        }
        ApiSellBack sellBack = new ApiSellBack();
        try {
            BeanUtils.copyProperties(sellBack, SellBack);
        } catch (Exception ex) {
        }
        List<Map> goodsList = sellBackManager.getGoodsList(SellBack.getId());
        for (Map goodsMap : goodsList) {
            goodsMap.put("goods_image", StaticResourcesUtil.convertToUrl(goodsMap.get(
                    "goods_image").toString()));
        }
        sellBack.setGoodsList(goodsList);
        return JsonResultUtil.getObjectJson(sellBack);
    }
    
    /**
     * 退换货列表
     * @param page
     * @return
     */
    @SuppressWarnings("unchecked")
	@ResponseBody
    @RequestMapping(value = "/sell-back-list", produces = MediaType.APPLICATION_JSON_VALUE)
    public JsonResult SellBack(Integer page,Integer pageSize) {
    	Integer member_id = CommonRequest.getMemberID();
        if (page == null || page <= 0) {
            page = 1;
        }
        if(pageSize == null || page <= 0){
        	pageSize = PAGE_SIZE;
        }
//        Member member = UserConext.getCurrentMember();
        if (member_id == null) {
            return JsonResultUtil.getErrorJson("请登录后再进行此项操作！");
        }
        Page webpage = sellBackManager.list(member_id, page.intValue(), pageSize);
        if (webpage == null) {
            return JsonResultUtil.getErrorJson("获取退换货数据失败，请您重试！");
        }
        List<Map> list = new ArrayList<Map>(); 
        		list = (List<Map>) webpage.getResult();
        for (Map map : list) {
            Integer id = (Integer) map.get("id");
            List<Map> goodsList = sellBackManager.getGoodsList(id);
            for (Map goodsMap : goodsList) {
                goodsMap.put("goods_image", StaticResourcesUtil.convertToUrl(goodsMap.get(
                        "goods_image").toString()));
            }
            map.put("goodsList", goodsList);
        }
        webpage.setCurrentPageNo(page);
        return JsonResultUtil.getObjectJson(webpage);
    }

    /**
     * 退款申请
     *
     * @param order_id       订单id
     * @param remark         【必填】 描述
     * @param reason         退货原因
     * @param ship_status    是否收到货
     * @param apply_alltotal 退款金额
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/refund", produces = MediaType.APPLICATION_JSON_VALUE)
    public JsonResult refund(int order_id, String remark,
                             String reason, Integer ship_status,
			double apply_alltotal) {
		try {
			Integer member_id = CommonRequest.getMemberID();
			Member member = memberManager.get(member_id);
			if (member == null) {
				return JsonResultUtil.getErrorJson("请您在登录后再申请退款！");
			}
			Order order = orderManager.get(order_id);
			if (order == null || !order.getMember_id().equals(member.getMember_id())) {
				return JsonResultUtil.getErrorJson("此订单不存在！");
			}
			// 记录会员信息
			SellBackList sellBack = new SellBackList();
			sellBack.setMember_id(member.getMember_id());
			sellBack.setSndto(member.getName());
			sellBack.setTradeno(com.enation.framework.util.DateUtil.toString(DateUtil.getDateline(), "yyMMddhhmmss"));// 退货单号
			sellBack.setOrderid(order_id);
			sellBack.setRegoperator("会员[" + member.getUname() + "]"); 
			sellBack.setTradestatus(0);
			sellBack.setRegtime(DateUtil.getDateline());
			sellBack.setRemark(remark);
			sellBack.setType(1);
			sellBack.setReason(reason);
			sellBack.setShip_status(ship_status.toString());
			sellBack.setApply_alltotal(apply_alltotal);
			sellBack.setRefund_way(order.getPayment_name());
			sellBack.setReturn_account(order.getPayment_account());
			this.sellBackManager.addSellBack(sellBack);
			return JsonResultUtil.getSuccessJson("申请退款成功，请等待客服人员审核！");
		} catch (RuntimeException e) {
			return JsonResultUtil.getErrorJson("申请退款失败，请您重试！");
		}
	}

    
    
    /**
     * 
     * 更改店铺优惠券
     * @param store_id  店铺ID
     * @param mcoup_id  优惠券ID
     * @return
     */
	@ResponseBody
	@RequestMapping(value = "/change-coupons", produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult changeArgsType_coupons(Integer addressId,Integer store_id, Integer mcoup_id) {
		/**
		Map storeData = new HashMap();
		Integer member_id = CommonRequest.getMemberID();
		List<Map> storeCartList = apiCartManager.countSelectPrice("yes", member_id, addressId);
		for (Map map : storeCartList) {
			Integer innerStoreid = (Integer)map.get(StoreCartKeyEnum.store_id.toString());
			if(store_id == innerStoreid){
				storeData =  map;
			}
		}
		List<Map> list = (List) storeData.get(StoreCartKeyEnum.goodslist.toString());
		OrderPrice orderPrice = (OrderPrice) storeData.get("storeprice");
		// 新增如果选择不使用优惠券，就将已经放进session中的店铺优惠券信息删除
		if (mcoup_id != 0) {
			MemberCoupons coupons = this.memberCouponsManager.get(mcoup_id);
			changeBonusForCoupons(orderPrice, coupons, store_id); //选择优惠券
		} else {
//			CouponsSession.cancelB2b2cCoupons(store_id);
			memberCouponsManager.releaseCoupons(member_id, store_id);
		}
//		return JsonResultUtil.getObjectJson(orderPrice, "storeprice");
		**/
		
		
		
		Map<String,Object> returnMap = new HashMap<String,Object>();
    	double totalMoney = 0.0d;
    	double goodsPrice = 0.0d;
    	
    	Integer member_id = CommonRequest.getMemberID();
    	String ticket = CommonRequest.getReqTicket();
		if (mcoup_id != 0) {
			memberCouponsManager.releaseCoupons(member_id, store_id);//先释放其余的优惠券
			MemberCoupons coupons = this.memberCouponsManager.get(mcoup_id);
//			changeBonusForCoupons(orderPrice, coupons, store_id); //选择优惠券
			memberCouponsManager.checkCoupons(coupons, 1);
		} else {
//			CouponsSession.cancelB2b2cCoupons(store_id);
			memberCouponsManager.releaseCoupons(member_id, store_id);
		}
		List<Map> cartItemList = apiCartManager.countSelectPrice("yes", member_id,addressId);
		if (cartItemList != null && cartItemList.size() > 0) {
			for (Map map : cartItemList) {
				List<StoreCartItem> List = (List) map.get("goodslist");
				for (StoreCartItem storeCartItem : List) {
					storeCartItem.setImage_default(StaticResourcesUtil.convertToUrl(storeCartItem.getImage_default()));
				}
				Integer storeId = NumberUtils.toInt(map.get("store_id").toString(), 0);
				
				//TODO  新增可用积分店铺和不可用积分店铺积分计算
				Store store = storeManager.getStore(storeId);
				boolean enablepoint = store.getEnablepoint() != 1;
				
				OrderPrice orderPrice = (OrderPrice) map.get("storeprice");
				totalMoney = CurrencyUtil.add(totalMoney, orderPrice.getNeedPayMoney());
				goodsPrice = CurrencyUtil.add(goodsPrice, orderPrice.getGoodsPrice());
				if (member_id != null) {
					List couponList = memberCouponsManager.getCouponsList(member_id, storeId,
							orderPrice.getGoodsPrice());
					map.put("couponList", couponList);
				}
			}
		}
		returnMap.put("cartItem", cartItemList);
		returnMap.put("totalPrice", totalMoney);
		//调用积分接口,可用积分为商品总价的10%,积分不够的话则按剩余积分算，积分与钱的比例为1:10
		int totalPoint = 0;
		try {
			totalPoint = crmPointManager.queryMemberPoint(ticket);
		} catch (Exception e) {
			logger.error("request member point error");
		}
		//根据商品价格计算可以使用的积分
		int canUsePoint = PointUtils.canUsePoint(goodsPrice, totalPoint);
		Double relief = PointUtils.usePointFacDiscount(canUsePoint);
		returnMap.put("canUsePoint", canUsePoint);//可使用积分
		returnMap.put("relief", relief);//可使用积分减免价格
		returnMap.put("reliefedPay", CurrencyUtil.sub(totalMoney, relief));//可使用积分后应付的价格
		return JsonResultUtil.getObjectJson(returnMap);
		
		
		
		
		
		// 商家可选积分方案***************************************************************
		
		
		
		
//		Map<String,Object> returnMap = new HashMap<String,Object>();
//    	double totalMoney = 0.0d;
////    	double goodsPrice = 0.0d;
//    	double pointgoodsPrice = 0.0d;
//    	
//    	Integer member_id = CommonRequest.getMemberID();
//    	String ticket = CommonRequest.getReqTicket();
//		if (mcoup_id != 0) {
//			memberCouponsManager.releaseCoupons(member_id, store_id);//先释放其余的优惠券
//			MemberCoupons coupons = this.memberCouponsManager.get(mcoup_id);
////			changeBonusForCoupons(orderPrice, coupons, store_id); //选择优惠券
//			memberCouponsManager.checkCoupons(coupons, 1);
//		} else {
////			CouponsSession.cancelB2b2cCoupons(store_id);
//			memberCouponsManager.releaseCoupons(member_id, store_id);
//		}
//		List<Map> cartItemList = apiCartManager.countSelectPrice("yes", member_id,addressId);
//		if (cartItemList != null && cartItemList.size() > 0) {
//			for (Map map : cartItemList) {
//				List<StoreCartItem> List = (List) map.get("goodslist");
//				for (StoreCartItem storeCartItem : List) {
//					storeCartItem.setImage_default(StaticResourcesUtil.convertToUrl(storeCartItem.getImage_default()));
//				}
//				Integer storeId = NumberUtils.toInt(map.get("store_id").toString(), 0);
//				
//				//TODO  新增可用积分店铺和不可用积分店铺积分计算
//				Store store = storeManager.getStore(storeId);
//				boolean enablepoint = store.getEnablepoint() != 1;
//				
//				OrderPrice orderPrice = (OrderPrice) map.get("storeprice");
//				totalMoney = CurrencyUtil.add(totalMoney, orderPrice.getNeedPayMoney());
//				
//				if(enablepoint){
//					pointgoodsPrice = CurrencyUtil.add(pointgoodsPrice, orderPrice.getGoodsPrice());
//				}
//				
////				goodsPrice = CurrencyUtil.add(goodsPrice, orderPrice.getGoodsPrice());
//				if (member_id != null) {
//					List couponList = memberCouponsManager.getCouponsList(member_id, storeId,
//							orderPrice.getGoodsPrice());
//					map.put("couponList", couponList);
//				}
//			}
//		}
//		returnMap.put("cartItem", cartItemList);
//		returnMap.put("totalPrice", totalMoney);
//		//调用积分接口,可用积分为商品总价的10%,积分不够的话则按剩余积分算，积分与钱的比例为1:10
//		int totalPoint = 0;
//		try {
//			totalPoint = crmPointManager.queryMemberPoint(ticket);
//		} catch (Exception e) {
//			logger.error("request member point error");
//		}
//		//根据商品价格计算可以使用的积分
//		int canUsePoint = PointUtils.canUsePoint(pointgoodsPrice, totalPoint);
//		Double relief = PointUtils.usePointFacDiscount(canUsePoint);
//		returnMap.put("canUsePoint", canUsePoint);//可使用积分
//		returnMap.put("relief", relief);//可使用积分减免价格
//		returnMap.put("reliefedPay", CurrencyUtil.sub(totalMoney, relief));//可使用积分后应付的价格
//		return JsonResultUtil.getObjectJson(returnMap);
		
		
		
	}
	
	
	
	@ResponseBody
	@RequestMapping(value = "/get-service-code", produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult getServiceGoodsInfo(@RequestParam String itemId) throws ParseException {
		if(StringUtil.isEmpty(itemId)){
			return JsonResultUtil.getErrorJson("itemId is null");
		}
		List<Map> sGoodsList = apiOrderManager.getCodeByParam(itemId);
		return JsonResultUtil.getObjectJson(sGoodsList);
	}
	
    
//    /**
//     * 
//     * 更改店铺优惠券
//     * @param store_id  店铺ID
//     * @param mcoup_id  优惠券ID
//     * @return
//     */
//	@ResponseBody
//	@RequestMapping(value = "/change-coupons", produces = MediaType.APPLICATION_JSON_VALUE)
//	public JsonResult changeArgsType_coupons(Integer region_id,Integer store_id, Integer mcoup_id) {
//		Map storeData = StoreCartContainer.getStoreData(store_id);
//		List list = (List) storeData.get(StoreCartKeyEnum.goodslist.toString());
//		String regionid_str = region_id == null ? "" : region_id + "";
//		Integer ship_id = null;
//		// 计算此配送方式时的店铺相关价格
//		OrderPrice orderPrice = this.cartManager.countPrice(list, ship_id, regionid_str);
//		// 激发计算子订单价格事件
//		orderPrice = storeCartPluginBundle.countChildPrice(orderPrice);
//		// 获取购物车中已经选择的商品的订单价格
//		OrderPrice storePrice = (OrderPrice) storeData.get("storeprice");
//		// 新增如果选择不使用优惠券，就将已经放进session中的店铺优惠券信息删除
//		if (mcoup_id != 0) {
//			MemberCoupons coupons = this.memberCouponsManager.get(mcoup_id);
//			changeBonusForCoupons(orderPrice, coupons, store_id);
//		} else {
//			CouponsSession.cancelB2b2cCoupons(store_id);
//		}
//		// 重新压入此店铺的订单价格和配送方式id
//		storeData.put(StoreCartKeyEnum.storeprice.toString(), orderPrice);
//		return JsonResultUtil.getObjectJson(orderPrice, "storeprice");
//	}
	
	/**
	 * 修改优惠券选项
	 * @param orderprice 订单价格
	 * @param coupons 优惠券信息
	 * @param storeid 店铺id
	 * @return
	 */
	private OrderPrice changeBonusForCoupons(OrderPrice orderprice, MemberCoupons coupons, Integer storeid) {
		// set 红包
//		CouponsSession.use(storeid, coupons);
		memberCouponsManager.checkCoupons(coupons, 1);
		//修改为从库里面查
		// 如果优惠券类型为满减券或者是现金券
		if (coupons.getMcoup_type() == 0 || coupons.getMcoup_type() == 2) {
			// 如果优惠券面额大于商品优惠价格的话 那么优惠价格为商品价格
			if (orderprice.getNeedPayMoney() <= coupons.getMcoup_money()) {
				orderprice.setDiscountPrice(orderprice.getNeedPayMoney());
				orderprice.setNeedPayMoney(0.0);
			} else {
				// 计算需要支付的金额
				orderprice.setNeedPayMoney(CurrencyUtil.add(orderprice.getNeedPayMoney(), -coupons.getMcoup_money()));
				orderprice.setDiscountPrice(coupons.getMcoup_money());
			}
		}
		// 如果优惠券类型为折扣券
		if (coupons.getMcoup_type() == 1) {
			double discountPercent = coupons.getMcoup_discount() / 100.00;
			double discountPrice = orderprice.getNeedPayMoney() - (orderprice.getNeedPayMoney() * discountPercent);
			orderprice.setNeedPayMoney(orderprice.getNeedPayMoney() * discountPercent);
			orderprice.setDiscountPrice(discountPrice);
		}

		return orderprice;
	}
	
	/**************************************************************************************************************************************/
	
	
	
	  /**
     * 改变店铺的配送方式以及红包<br>
     * 调用此api时必须已经访问过购物车列表<br>
     * @return 含有价格信息的json串
     */
    @ResponseBody
    @RequestMapping(value = "change-ship-bonus")
    public JsonResult changeShipAndBonus(Integer regionid, Integer[] store_ids, Integer[] type_ids, Integer[] bonus_ids) {
        if (store_ids == null || store_ids.length == 0 || type_ids == null || type_ids.length == 0 || bonus_ids == null || bonus_ids.length == 0) {
            return JsonResultUtil.getErrorJson("系统参数错误！");
        }
        if (store_ids.length != type_ids.length || type_ids.length != bonus_ids.length) {
            return JsonResultUtil.getErrorJson("系统参数错误！");
        }
        OrderPrice orderPrice = null;
        for (int i = 0; i < store_ids.length; i++) {
            orderPrice = this.changeArgsType(regionid, store_ids[i], type_ids[i], bonus_ids[i]);
        }
        return JsonResultUtil.getObjectJson(orderPrice);
    }
    
    
    /**
     * 更改配送方式及优惠券
     * @param regionid
     * @param store_id
     * @param type_id
     * @param bonus_id
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	private OrderPrice changeArgsType(Integer regionid, Integer store_id, Integer type_id, Integer bonus_id) {
		StoreMember member = this.storeMemberManager.getStoreMember();
		// 由购物车列表中获取此店铺的相关信息
		Map storeData = StoreCartContainer.getStoreData(store_id);
		// 获取此店铺的购物列表
		List list = (List) storeData.get(StoreCartKeyEnum.goodslist.toString());
		// 配送地区
		String regionid_str = regionid == null ? "" : regionid + "";
		// 计算此配送方式时的店铺相关价格
		OrderPrice orderPrice = this.cartManager.countPrice(list, type_id, regionid_str);
		// 激发计算子订单价格事件
		orderPrice = storeCartPluginBundle.countChildPrice(orderPrice);
		// 获取购物车中已经选择的商品的订单价格
		OrderPrice storePrice = (OrderPrice) storeData.get("storeprice");
		Double act_discount = storePrice.getActDiscount();

		// 如果促销活动优惠的现金不为空
		if (act_discount != null && act_discount != 0) {
			orderPrice.setActDiscount(act_discount);
			orderPrice.setNeedPayMoney(orderPrice.getNeedPayMoney() - act_discount);
		}
		//废弃代码bak()

		// 新增如果选择不使用优惠券，就将已经放进session中的店铺优惠券信息删除
		if (bonus_id != 0) {
			MemberBonus bonuss = this.b2b2cBonusManager.getOneMyBonus(member.getMember_id(), store_id, bonus_id);
			changeBonus(orderPrice, bonuss, store_id);
		} else {
			B2b2cBonusSession.cancelB2b2cBonus(store_id);
		}
		// 重新压入此店铺的订单价格和配送方式id
		storeData.put(StoreCartKeyEnum.storeprice.toString(), orderPrice);
		storeData.put(StoreCartKeyEnum.shiptypeid.toString(), type_id);
		return orderPrice;
	}

    /**
	 * 修改优惠券选项
	 * @author liushuai
	 * @version v1.0,2015年9月22日18:21:33
	 * @param bonuss
	 * @since v1.0
	 */
	private OrderPrice changeBonus(OrderPrice orderprice, MemberBonus bonus, Integer storeid) {
		// set 红包
		B2b2cBonusSession.useBonus(storeid, bonus);
		// 如果优惠券面额大于商品优惠价格的话 那么优惠价格为商品价格
		if (orderprice.getNeedPayMoney() <= bonus.getType_money()) {
			orderprice.setDiscountPrice(orderprice.getNeedPayMoney());
			orderprice.setNeedPayMoney(0.0);
		} else {
			// 计算需要支付的金额	
			orderprice.setNeedPayMoney(CurrencyUtil.add(orderprice.getOrderPrice(), -bonus.getType_money()));
			orderprice.setDiscountPrice(bonus.getType_money());
		}
		return orderprice;
	}
    
    /**
	 * 获取参加促销活动商品价格总计
	 * @param cartItemList
	 * @return
	 */
	private Double getTotalPrice(List<StoreCartItem> cartItemList) {
		Double actTotalPrice = 0d;
		Double sameGoodsTotal = 0d;
		for (StoreCartItem cartItem : cartItemList) {
			Integer activity_id = cartItem.getActivity_id();
			// 如果促销活动信息ID不为空
			if (activity_id != null) {
				sameGoodsTotal = CurrencyUtil.mul(cartItem.getPrice(), cartItem.getNum());
				actTotalPrice = CurrencyUtil.add(actTotalPrice, sameGoodsTotal);
			}
		}
		return actTotalPrice;
	}
	
	/**
	 * 作为废弃代码备份
	 */
	public void  bak(){
//		public JsonResult changeArgsType_coupons(Integer region_id, Integer store_id, Integer type_id, Integer mcoup_id) {
//			// 由购物车列表中获取此店铺的相关信息
//			Map storeData = StoreCartContainer.getStoreData(store_id);
//			// 获取此店铺的购物列表
//			List list = (List) storeData.get(StoreCartKeyEnum.goodslist.toString());
//			// 配送地区
//			String regionid_str = region_id == null ? "" : region_id + "";
//			// 计算此配送方式时的店铺相关价格
//			OrderPrice orderPrice = this.cartManager.countPrice(list, type_id, regionid_str);
//			// 激发计算子订单价格事件
//			orderPrice = storeCartPluginBundle.countChildPrice(orderPrice);
//			// 获取购物车中已经选择的商品的订单价格
//			OrderPrice storePrice = (OrderPrice) storeData.get("storeprice");
//			Double act_discount = storePrice.getActDiscount();
//			// 如果促销活动优惠的现金不为空
//			if (act_discount != null && act_discount != 0) {
//				orderPrice.setActDiscount(act_discount);
//				orderPrice.setNeedPayMoney(orderPrice.getNeedPayMoney() - act_discount);
//			}
//			// 新增如果选择不使用优惠券，就将已经放进session中的店铺优惠券信息删除
//			if (mcoup_id != 0) {
//				MemberCoupons coupons = this.memberCouponsManager.get(mcoup_id);
//				changeBonusForCoupons(orderPrice, coupons, store_id);
//			} else {
//				CouponsSession.cancelB2b2cCoupons(store_id);
//			}
//			// 重新压入此店铺的订单价格和配送方式id
//			storeData.put(StoreCartKeyEnum.storeprice.toString(), orderPrice);
//			storeData.put(StoreCartKeyEnum.shiptypeid.toString(), type_id);
//			return JsonResultUtil.getObjectJson(orderPrice, "storeprice");
//		}
		
		
		
		//关于店铺活动的方法
//		Integer activity_id = (Integer) storeData.get("activity_id");
		// 如果促销活动id不为空
//		if (activity_id != null) {
//			// 获取参加促销活动商品价格总计
//			Double actTotalPrice = this.getTotalPrice(list);
//			ActivityDetail detail = this.activityDetailManager.getDetail(activity_id);
//			// 增加判断：如果参加促销活动商品价格总计大于或者等于促销活动一系列优惠的基础钱数
//			if (actTotalPrice >= detail.getFull_money()) {
//				// 如果促销活动包含了免运费的优惠内容
//				if (detail.getIs_free_ship() == 1) {
//					orderPrice.setIs_free_ship(1);
//					orderPrice.setAct_free_ship(orderPrice.getShippingPrice());
//					orderPrice.setShippingPrice(0d);
//				}
//				// 如果促销含有送积分的活动
//				if (detail.getIs_send_point() == 1) {
//					orderPrice.setPoint(detail.getPoint_value());
//					// 修复促销活动赠送积分，选中优惠券后没有送积分的bug
//					orderPrice.setActivity_point(detail.getPoint_value());
//				}
//				// 如果促销含有送赠品的活动
//				if (detail.getIs_send_gift() == 1) {
//					// 获取赠品的可用库存
//					Integer enable_store = this.storeActivityGiftManager.get(detail.getGift_id()).getEnable_store();
//					// 如果赠品的可用库存大于0
//					if (enable_store > 0) {
//						orderPrice.setGift_id(detail.getGift_id());
//					}
//				}
//				// 如果促销含有送优惠券的活动
//				if (detail.getIs_send_bonus() == 1) {
//					// 获取店铺优惠券信息
//					StoreBonusType bonus = this.b2b2cBonusManager.getBonus(detail.getBonus_id());
//					// 优惠券发行量
//					int createNum = bonus.getCreate_num();
//					// 获取优惠券已被领取的数量
//					int count = this.b2b2cBonusManager.getCountBonus(detail.getBonus_id());
//					// 如果优惠券的发行量大于已经被领取的优惠券数量
//					if (createNum > count) {
//						orderPrice.setBonus_id(detail.getBonus_id());
//					}
//				}
//			}
//		}
		
	}
	

//	@ResponseBody
//	@RequestMapping(value = "/order-attention", produces = MediaType.APPLICATION_JSON_VALUE)
//	public JsonResult orderAttention(@RequestParam(value = "invokeNum", required = true) String invokeNum) {
//		try {
//			if (StringUtils.isBlank(invokeNum)) {
//				return JsonResultUtil.getErrorJson("INVOKE NUM IS ERROR");
//			}
//			String store_id = apiStoreAttentionManager.queryStoreIdByInvokeNum(invokeNum);
//			if (StringUtils.isEmpty(store_id)) {
//				return JsonResultUtil.getErrorJson("INVALID INVOKE NUM");
//			}
//			boolean hasNewOrder = apiStoreAttentionManager.hasNewCreateOrder(store_id);
//			Map map = new HashMap();
//			map.put("hasNewOrder", hasNewOrder);
//			return JsonResultUtil.getObjectJson(map);
//		} catch (Exception e) {
//			this.logger.error("", e);
//			return JsonResultUtil.getErrorJson("获取支付方式出错[" + e.getMessage() + "]");
//		}
//	}
	
	
	@ResponseBody
	@RequestMapping(value = "/order-attention", produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonResult orderAttention(@RequestParam(value = "storeId", required = true) String storeId) {
		try {
			if (StringUtils.isBlank(storeId)) {
				return JsonResultUtil.getErrorJson("INVOKE NUM IS ERROR");
			}
//			String store_id = apiStoreAttentionManager.queryStoreIdByInvokeNum(invokeNum);
//			if (StringUtils.isEmpty(store_id)) {
//				return JsonResultUtil.getErrorJson("INVALID INVOKE NUM");
//			}
			boolean hasNewOrder = apiStoreAttentionManager.hasNewCreateOrder(storeId);
			Map map = new HashMap();
			map.put("hasNewOrder", hasNewOrder);
			return JsonResultUtil.getObjectJson(map);
		} catch (Exception e) {
			this.logger.error("", e);
			return JsonResultUtil.getErrorJson("获取支付方式出错[" + e.getMessage() + "]");
		}
	}

}
