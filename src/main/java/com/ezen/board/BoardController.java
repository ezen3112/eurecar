package com.ezen.board;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ezen.eure.AdminDTO;
import com.ezen.eure.HowDTO;
import com.ezen.eure.LoginDTO;
import com.ezen.eure.OneDTO;
import com.ezen.eure.Service;


@Controller
public class BoardController {
	@Autowired
	SqlSession sqlSession;
	
	//1:1문의
	@RequestMapping(value="/qs")
	public String qs(HttpServletRequest request, Model mo ,RedirectAttributes ra)
	{
        HttpSession hs=request.getSession();
        if(hs.getAttribute("loginstate").equals(true))
        {
        	    return "onenotice";
        }
        else if(hs.getAttribute("adminstate").equals(true))
        {
        	return "onenotice";
        }
        return "redirect:logingo2";
	}
	
	@RequestMapping(value="/logingo2")
	public String logingo()
	{
		return "loginnotice";
	}

	
	@RequestMapping(value="/login2",method = RequestMethod.POST)
	public ModelAndView login(HttpServletRequest request,  RedirectAttributes ra) {
		ModelAndView mav = new ModelAndView();
		String id = request.getParameter("id");
		String pw = request.getParameter("pw");
		String email = request.getParameter("email");
		Service ser = sqlSession.getMapper(Service.class);
		LoginDTO dto = ser.login(id, pw, email);
		if(dto!=null)
		{
			HttpSession hs = request.getSession();
			hs.setAttribute("member", dto);
			hs.setAttribute("loginstate", true);
			hs.setMaxInactiveInterval(300);
			mav.setViewName("onenotice");
		}
		else 
		{
			ra.addAttribute("result","loginfail");
			mav.setViewName("redirect:logingo2");
		}
		return mav;
	}
	
	

	@RequestMapping(value="/qsave", method = RequestMethod.POST)
	public String onenoticesave(HttpServletRequest request) {
		String title=request.getParameter("title");
		String content=request.getParameter("content");
		String qlist=request.getParameter("qlist");
		int custnum = Integer.parseInt(request.getParameter("custnum"));
		Service ser = sqlSession.getMapper(Service.class);
		ser.qsinsert(qlist, title, content, custnum);
		return "onenotice";
	}	
	
	@RequestMapping(value="/qsout")
	public String qsout(HttpServletRequest request, Model mo) {
		int custnum = Integer.parseInt(request.getParameter("custnum"));
		Service ser = sqlSession.getMapper(Service.class);
		ArrayList<OneDTO> list = ser.qsout(custnum);
		mo.addAttribute("list", list);
		return "qsout";
	}
	
	@RequestMapping(value="/onedetail")
	public String detail(HttpServletRequest request, Model mo) {
		int num = Integer.parseInt(request.getParameter("num"));
		
		Service ser = sqlSession.getMapper(Service.class);
		OneDTO dto = ser.qsdetail(num);
		mo.addAttribute("dto",dto);
		return "onedetail";
	}
	
	@RequestMapping(value="/qsout2")
	public String backqslist(HttpServletRequest request, Model mo) {
		int custnum = Integer.parseInt(request.getParameter("custnum"));
		Service ser = sqlSession.getMapper(Service.class);
		ArrayList<OneDTO> list = ser.qsout(custnum);
		mo.addAttribute("list", list);
		
		return "qsout";
	}
	
	   //공지사항
	  @RequestMapping(value="/notice")
	   public String NOTICE(HttpServletRequest request,Model mo) {
		  String id = request.getParameter("id");
		  Service ser = sqlSession.getMapper(Service.class);
		  AdminDTO ndto = ser.noticeid(id);
		  HttpSession session=request.getSession();
			if( (Boolean) session.getAttribute("adminstate"))
			{	
				  NoticeService nser = sqlSession.getMapper(NoticeService.class);
				   ArrayList<NoticeDTO> nlist = nser.boardout();
				   mo.addAttribute("nlist",nlist);
				   mo.addAttribute("ndto",ndto);
			    return "hostboard";
			}
			else if( (Boolean) session.getAttribute("loginstate"))
			{	
				return "redirect:board";
			}
			return "redirect:board";
		}
	   //공지사항 끝	
	   //호스트 공지사항 글쓰기 시작
	
	   @RequestMapping(value="/hostboardinput")
	   public String hostboardinput(HttpServletRequest request, Model mo) {
		   String id = request.getParameter("id");
		   Service ser = sqlSession.getMapper(Service.class);
		   AdminDTO ndto = ser.noticeid(id);
		   mo.addAttribute("ndto",ndto);
		   return "hostboardinput";
	   }
	   @RequestMapping(value="/hostboardsave")
	   public String hostboardsave(HttpServletRequest request) {
		   String ntitle = request.getParameter("ntitle");
		   String ncontent = request.getParameter("ncontent");
		   String id = request.getParameter("id");
		   NoticeService nser = sqlSession.getMapper(NoticeService.class);
		   nser.hostboardinsert(ntitle,ncontent,id);
		   return "redirect:hostboard2";
	   }
	   //호스트 공지사항 글쓰기 끝
	   //호스트 공지사항 결과보기 시작
	   @RequestMapping(value="/board")
	   public String boardout(Model mo) {
		   NoticeService nser = sqlSession.getMapper(NoticeService.class);
		   ArrayList<NoticeDTO> nlist = nser.boardout();
		   mo.addAttribute("nlist",nlist);
		   return "board";
	   }
	   
	   @RequestMapping(value="/hostboard2")
	   public String hostboardout2(HttpServletRequest request, Model mo) {
		    	NoticeService nser = sqlSession.getMapper(NoticeService.class);
			   ArrayList<NoticeDTO> nlist = nser.boardout();
			   mo.addAttribute("nlist",nlist);
			  
		   return "hostboard";
	   }
	   
	 

	   //호스트 공지사항 결과보기 끝 1234
}

