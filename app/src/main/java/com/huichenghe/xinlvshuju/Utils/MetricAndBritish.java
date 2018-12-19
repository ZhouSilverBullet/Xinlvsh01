package com.huichenghe.xinlvshuju.Utils;

import java.math.BigDecimal;

public class MetricAndBritish 
{
	
	/**
	 * 英寸转化为cm
	 * @param height
	 * @return
	 * 1cm = 0.39英寸
	 */
	public int BritishToMetricInInch(int height)
	{
			float i = (float)(height / 0.39);
			BigDecimal d = new BigDecimal(i);
			d = d.setScale(0, BigDecimal.ROUND_HALF_UP);
		return d.intValue();
	}
	
	/**
	 * 1kg = phone.20磅
	 * @param weight
	 * @return
	 */
	public int BritishToMetricInlb(int weight)
	{
		float w = (float)(weight / 2.20);
		BigDecimal d = new BigDecimal(w);
		d.setScale(0, BigDecimal.ROUND_HALF_UP);
		
		
		
			return d.intValue();
	}
	
	public int MetricToBritishInCm(int height)
	{
		float h = (float)(height * 0.3937f);
		BigDecimal b = new BigDecimal(h);
		b.setScale(0, BigDecimal.ROUND_HALF_UP);
		return b.intValue();
	}
	
	public int MetricToBritishInKg(int height)
	{
		float h = (float)(height * 2.2005f);
		BigDecimal b = new BigDecimal(h);
		b.setScale(0, BigDecimal.ROUND_HALF_UP);
		return b.intValue();
	}


	/**
	 * 1kg = 0.6213mi
	 * @param kg
	 * @return
	 */
	public float kilometreToMile(float kg)
	{
		float mile = (float)(kg * 0.6213);
		BigDecimal b = new BigDecimal(mile);
		b = b.setScale(2, BigDecimal.ROUND_HALF_UP);
		return b.floatValue();
	}
	
	

}
