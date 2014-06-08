package wissim.controller.filters;

import java.text.ParseException;

import javax.swing.RowFilter;

/**
 * Interface dinh nghia nhung yeu cau ve viec parse nhung filter expressions
 * 
 * @author Tien
 *
 */
public interface IParser {
	/*Parse text input, tra ve mot filter de su dung cho table*/
	RowFilter<?, ?> parseText(String expression) throws ParseException;
	/*Parse instant*/
	InstantFilter parseInstantText(String expression) throws ParseException;
	/*Ket thuc mot filter expression*/
	String escape(String s);
	/*Remove cac tag Html khoi du lieu nhap vao, chuyen cac ki tu Html thanh ki tu Java*/
	String stripHtml(String s);
	/*Instant Filter*/
	public class InstantFilter{
		public RowFilter	filter;
		public String		expression;
	}
}
