package com.enation.app.shop.core.order.action;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.enation.app.shop.core.order.model.PayCfg;
import com.enation.app.shop.core.order.service.IPaymentManager;
import com.enation.eop.processor.core.Response;
import com.enation.framework.action.GridController;
import com.enation.framework.action.GridJsonResult;
import com.enation.framework.action.JsonResult;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.JsonResultUtil;

/**
 * 支付配置action
 * @author kingapex
 *2010-4-13下午05:58:35
 *@author LiFenLong 2014-4-2;2.0改版
 */
@Controller
@Scope("prototype")
@RequestMapping("/shop/admin/payCfg")
public class PayCfgController extends GridController {
	
	@Autowired
	private IPaymentManager paymentManager ;
	
	/**
	 * 跳转至付款方式列表
	 * @return 付款方式列表
	 */
	
	@RequestMapping("/list")
	public String list(){
		return "/shop/admin/payment/payment_list";
	}
	/**
	 * @author LiFenLong
	 * @param list 付款方式列表 
	 * @return 付款方式列表Json
	 */
	@ResponseBody
	@RequestMapping("/list-json")
	public GridJsonResult listJson(){
		return JsonResultUtil.getGridJson(paymentManager.list());
	}
	
	
	/**
	 * 到添加页 
	 * @param pluginList 支付方式插件列表
	 * @return 支付添加页
	 */
	@RequestMapping("/add")
	public ModelAndView add(){	
		ModelAndView view= new ModelAndView();
		view.setViewName("/shop/admin/payment/payment_add");
		view.addObject("pluginList", paymentManager.listAvailablePlugins());
		return view;
	}
	/**
	 * 获取支付插件的Html
	 * @param pluginId 插件Id
	 * @param paymentId 支付方式Id
	 * @return json
	 */
	@ResponseBody
	@RequestMapping(value="/get-plugin-html",produces="text/html;charset=UTF-8")
	public String getPluginHtml(Integer paymentId,String pluginId){
		try{
			return paymentManager.getPluginInstallHtml(pluginId, paymentId);
		}catch(RuntimeException e){
			e.printStackTrace();
			return e.getMessage();
		}
	}
	
	/**
	 * 到修改页面
	 * @param pluginList 支付方式插件列表,List
	 * @param paymentId 支付方式Id,Integer
	 * @param name 支付方式名称,String
	 * @param type 支付方式类型,Integer
	 * @param biref 支付方式介绍,String
	 * @return 修改页面
	 */
	@RequestMapping("/edit")
	public ModelAndView  edit(Integer paymentId,String name){
		
		ModelAndView view= new ModelAndView();
		PayCfg cfg= paymentManager.get(paymentId);
		view.addObject("pluginList", paymentManager.listAvailablePlugins());
		view.addObject("cfg",cfg );
		view.addObject("name", cfg.getName());
		view.addObject("type", cfg.getType());
		view.addObject("biref", cfg.getBiref());
		view.addObject("paymentId", paymentId);
		
		view.setViewName("/shop/admin/payment/payment_edit");
		return view;
	}
	
	
	/**
	 * 保存添加
	 * @param name 支付方式名称,String
	 * @param type 支付方式类型,Integer
	 * @param biref 支付方式介绍,String
	 * @return result
	 * result 1.操作成功.0.操作失败
	 */
	@ResponseBody
	@RequestMapping(value="/save-add")
	public Object saveAdd(String name,String type,String biref){
		try{
			HttpServletRequest  request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
			Enumeration<String> names = request.getParameterNames();
			Map<String,String> params = new HashMap<String, String>();
			while(names.hasMoreElements()){
				String nname= names.nextElement();
				
				if("name".equals(nname)) continue;
				if("type".equals(nname)) continue;
				if("biref".equals(nname)) continue;
				if("paymentId".equals(nname)) continue;
				if("submit".equals(nname)) continue;
				String value  = request.getParameter(nname);
				params.put(nname, value);
			}
			PayCfg pay=paymentManager.getPayCfgByName(type);
			if (pay != null) {
				
				return JsonResultUtil.getErrorJson("支付方式添加失败,不能添加重复的支付方式");
			} else {
				Integer id = this.paymentManager.add(name, type, biref, params);
				
				Map map=new HashMap();
				map.put("result", 1);
				map.put("message","支付方式添加成功" );
				map.put("id", id);
				return map;
			}
		}catch(RuntimeException e){
			logger.error("支付方式添加失败", e);
			return JsonResultUtil.getErrorJson("支付方式添加失败");
		} 		
	}
	
	/**
	 * 保存
	 * @param paymentId 支付方式Id,Integer
	 * @param saveAdd(),添加
	 * @param saveEdit(),修改
	 * @return
	 */
	public Object save(Integer paymentId,String type,String name,String biref){
		
		if(paymentId==null || "".equals(paymentId)){
			return this.saveAdd(name,type,biref);
		}else{
			return this.saveEdit(paymentId,type,type,biref);
		}
		
	}
	
	
	/**
	 * 保存修改 
	 * @param paymentId 支付方式Id,Integer
	 * @param name 支付方式名称,String
	 * @param type 支付方式类型,Integer
	 * @param biref 支付方式介绍,String
	 * @return result
	 * result 1.操作成功.0.操作失败
	 */
	@ResponseBody
	@RequestMapping(value="/save-edit")
	public JsonResult saveEdit(Integer paymentId,String type,String name,String biref){
		try{
			HttpServletRequest  request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
			Enumeration<String> names = request.getParameterNames();
			Map<String,String> params = new HashMap<String, String>();
			while(names.hasMoreElements()){
				String nname= names.nextElement();
				
				if("name".equals(nname)) continue;
				if("type".equals(nname)) continue;
				if("biref".equals(nname)) continue;
				if("paymentId".equals(nname)) continue;
				if("submit".equals(nname)) continue;
				String value  = request.getParameter(nname);
				params.put(nname, value);
			}
			
			this.paymentManager.edit(paymentId,name,type, biref, params);
			return JsonResultUtil.getSuccessJson("支付方式修改成功");
		}catch(RuntimeException e){
			logger.error("支付方式修改失败", e);
			return JsonResultUtil.getErrorJson("支付方式修改失败");
		}
	}
	
	
	/**
	 * 删除
	 * @param id 支付方式Id数组,Integer[]
	 * @return result
	 * result 1.操作成功.0.操作失败
	 */
	@ResponseBody
	@RequestMapping(value="/delete")
	public JsonResult delete( Integer[] id){
		try{ 		 
			// 如果 剩余的支付方式总类数量小于等于 要删除的 支付方式总类数量，那么制造一个错误，让这个程序提出一个异常前台删除失败
			if (id.length >= paymentManager.getCount()) {
				return JsonResultUtil.getErrorJson("不能删除全部支付方式");
			}
			this.paymentManager.delete(id);
			return JsonResultUtil.getSuccessJson("支付方式删除成功");
		}catch(Exception e){
			logger.error("支付方式删除失败", e);
			return JsonResultUtil.getErrorJson("支付方式删除失败");
		}
	}
	
}
