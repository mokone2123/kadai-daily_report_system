package controllers.login;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import models.Employee;
import models.Report;
import utils.DBUtil;

/**
 * Servlet implementation class LogoutServlet
 */
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public LogoutServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 今日の分の日報に退勤時間を記載する
        EntityManager em = DBUtil.createEntityManager();

        Employee login_employee = (Employee)request.getSession().getAttribute("login_employee");
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());

        List<Report> reports = em.createNamedQuery("getTodayMyReports", Report.class)
                .setParameter("employee", login_employee)
                .setParameter("work_time", currentTime)
                .getResultList();

        Iterator<Report> iterator = reports.iterator();
        while(iterator.hasNext()){
            Report r = (Report)iterator.next();
            r.setWork_time(currentTime);
        }
        em.getTransaction().begin();
        em.getTransaction().commit();
        em.close();

        // ログアウトする
        request.getSession().removeAttribute("login_employee");

        request.getSession().setAttribute("flush", "ログアウトしました。");

        response.sendRedirect(request.getContextPath() + "/login");
    }

}
