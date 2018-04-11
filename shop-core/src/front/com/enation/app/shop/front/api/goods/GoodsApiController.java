package com.enation.app.shop.front.api.goods;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.enation.app.shop.core.goods.model.Goods;
import com.enation.app.shop.core.goods.model.Product;
import com.enation.app.shop.core.goods.service.IGoodsManager;
import com.enation.app.shop.core.goods.service.IProductManager;
import com.enation.framework.action.GridController;
import com.enation.framework.action.JsonResult;
import com.enation.framework.util.JsonMessageUtil;
import com.enation.framework.util.JsonResultUtil;

/**
 * 商品api
 * @author kingapex
 *2013-8-20下午8:17:14
 */


@Scope("prototype")
@Controller 
@RequestMapping("/api/shop/goods")
public class GoodsApiController  extends GridController {
	
	@Autowired
	private IProductManager productManager;

	@Autowired
	private IGoodsManager goodsManager;
	
	/**
	 * 搜索商品
	 * 输入参数：
	 * @param catid ：分类id,如果不输入，则搜索全部的分类下的商品
	 * @param brandid:品牌id，如果不佃入，是搜索全部的品牌下的商品
	 * @param keyword：搜索关键字，会搜索商品名称和商品编号
	 * @return 商品搜索结果 
	 * {@link Goods}
	 */
	@ResponseBody
	@RequestMapping(value="/search",produces = MediaType.APPLICATION_JSON_VALUE)
	public String search(Integer goodsid,Integer catid,String keyword,Integer brandid){ 
		
		Map goodsMap=new HashMap();
		
		goodsMap.put("catid", catid);
		goodsMap.put("brandid", brandid);
		goodsMap.put("keyword", keyword);
		goodsMap.put("stype", 0);
		
		List<Goods>  goodslist =goodsManager.searchGoods(goodsMap);

		return JsonMessageUtil.getListJson(goodslist);
		
	}
	
	/***
	 * 
	 * @param goodsid 商品Id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/product-list", produces = MediaType.APPLICATION_JSON_VALUE)
	public Object productList(int goodsid){
		try {
			List<Product> productList  = this.productManager.list(goodsid);
			StringBuffer str  = new StringBuffer();
			for (Product product : productList) {
				if(str.length()!=0){str.append(",");}
				str.append("{\"product_id\":"+product.getProduct_id()+",");
				str.append("\"goods_id\":"+product.getGoods_id()+",");
				str.append("\"sn\":\""+product.getSn()+"\",");
				str.append("\"store\":"+product.getStore()+",");
				str.append("\"enable_store\":"+product.getEnable_store()+",");
				str.append("\"price\":"+product.getPrice()+",");
				str.append("\"specs\":"+product.getSpecsvIdJson()+"}");
			 
			}
			
			return  "{\"result\":1,\"data\":["+str+"]}";
			
			
		} catch (Exception e) {
			this.logger.error("获取产品列表出错", e);
			return JsonResultUtil.getErrorJson("获取产品列表出错:"+e.getMessage());
		}
	}

}
