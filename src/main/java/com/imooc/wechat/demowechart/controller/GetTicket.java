package com.imooc.wechat.demowechart.controller;

import com.imooc.wechat.demowechart.service.WxTokeService;
import org.springframework.core.annotation.Order;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Servlet implementation class GetTicket
 */
@WebServlet("/GetTicket")
public class GetTicket extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		String ticket = WxTokeService.getQrCodeTicket();
		out.print(ticket);
		out.flush();
		out.close();
	}
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
