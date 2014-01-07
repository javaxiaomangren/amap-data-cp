package com.amap.data.base.fieldcheck;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FieldLenCheck {
	// **********tinyint range**********
	private static int minTinyint = -128;
	private static int maxTinyint = 127;
	
	// **********smallint range**********
	private static int minSmallint = -32768;
	private static int maxSmallint = 32767;
	
	// **********int range**********
	private static int minInt = -2147483648;
	private static int maxInt = 2147483647;
	
	// **********bigint range****************
	private static long minBigint = -9223372036854774808L;
	private static long maxBigint = 9223372036854775807L;

	//**********double**********
	// negative double range
	private static double negativeMinDouble = (double) -1.7976931348623157E+308;
	private static double negativeMaxDouble = (double) -2.2250738585072014E-308;

	// 0
	private static double zeroDouble = (double) 0;

	// positive double range
	private static double positiveMinDouble = (double) 2.2250738585072014E-308;
	private static double positiveMaxDouble = (double) 1.7976931348623157E+308;
	
	// **********folat**********
	// negative float range
	private static float negativeMinFloat = (float) -3.402823466E+38;
	private static float negativeMaxFloat = (float) -1.175494351E-38;

	// 0
	private static float zeroFloat = (float) 0;

	// positive float range
	private static float positiveMinFloat = (float) 1.175494351E-38;
	private static float positiveMaxFloat = (float) 3.402823466351E+38;

	//********************the following are functions********************
	//**********tinyint**********
	public boolean TinyintJudge(int tinyint) {
		if (tinyint > minSmallint && tinyint < maxTinyint) {
			return true;
		}

		return false;
	}
	
	// **********smallint**********
	public boolean SmallintJudge(int smallint) {
		if (smallint > minTinyint && smallint < maxSmallint) {
			return true;
		}

		return false;
	}

	//**********int**********
	public boolean IntJudge(int i) {
		if (i > minInt && i < maxInt) {
			return true;
		}

		return false;
	}

	
	//**********bigint**********
	public boolean BigintJudge(long l) {
		if (l > minBigint && l < maxBigint) {
			return true;
		}

		return false;
	}
	
	//**********double**********
	public boolean DoubleJudge(double d){
		//d > 0
		if( d > zeroDouble){
			//positive judge
			if(d > positiveMinDouble && d < positiveMaxDouble){
				return true;
			}
			
		}else if( d == zeroDouble){
			//zore judge
			return true;
			
		}else{
			//negative judge
			if( d > negativeMinDouble && d < negativeMaxDouble ){
				return true;
			}
		}
		
		return false;
	}
	
	//**********float**********
	public boolean FloatJudge(float f){
		//f > 0
		if( f > zeroFloat){
			//positive judge
			if(f > positiveMinFloat && f < positiveMaxFloat){
				return true;
			}
			
		}else if( f == zeroFloat){
			//zore judge
			return true;
			
		}else{
			//negative judge
			if( f > negativeMinFloat && f < negativeMaxFloat ){
				return true;
			}
			
		}
		
		return false;
	}
	
	
	//GetVarcharLenth
	public int GetVarcharLenth(String varStr) {
		int len = 0;
		for (int i = 0; i < varStr.length(); i++) {
			char c = varStr.charAt(i);

			if (isChinese(c)) {
				len += 3;
			} else {
				len += 1;
			}
		}

		return len;
	}

	// judge whether c is chinese
	public static boolean isChinese(char c) {
		
		String regEx = "[\u4e00-\u9fa5]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(c + "");
		if (m.find()) {
			return true;
		}
		return false;
	}
	public static void main(String [] args){
		String alias="ULO PARK悠乐汇 首都机场沿线,望京";
		if(alias.contains(" ")){
			String ff[]=alias.split(" ");
			String regEx = "[\u4e00-\u9fa5]";
			Pattern p = Pattern.compile(regEx);
			alias=ff[0];
			for(int i=1;i<ff.length;i++){
				Matcher m = p.matcher(ff[i] + "");
				if (m.find()) {
					String str=alias.substring(0,2);
					 if(str.matches("^[a-zA-Z]*"))
					 {
						 alias+=" "+ff[i]; 
					
					 }
					 break;
					 
				}else{
					alias+=" "+ff[i];
					System.out.print(alias);
				}
			}
		}
	}

}
