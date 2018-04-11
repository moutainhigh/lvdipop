package com.enation.app.shop.core.order.model;

import com.enation.framework.database.PrimaryKeyField;



/**
 * PayCfg generated by MyEclipse - Hibernate Tools
 */

public class PayCfg  implements java.io.Serializable {


    // Fields    

     protected Integer id;

	protected String name;

	protected String config;

	protected String biref;

	protected Double pay_fee;

	protected String type;

	public String getBiref() {
		return biref;
	}

	public void setBiref(String biref) {
		this.biref = biref;
	}
	public String getConfig() {
		return config;
	}
	public void setConfig(String config) {
		this.config = config;
	}
	@PrimaryKeyField
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Double getPay_fee() {
		return pay_fee;
	}
	public void setPay_fee(Double pay_fee) {
		this.pay_fee = pay_fee;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

 
     






}