package com.payremindme.api.config;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;

public class ReportCompiller {

    public static void main(String ...args) throws JRException {

        JasperCompileManager.compileReportToFile(
                "C:\\development\\payremind-me-api\\docs\\relatorios\\lancamentos-por-pessoa.jrxml",
               "C:\\development\\payremind-me-api\\docs\\relatorios\\lancamentos-por-pessoa.jasper");

        System.out.println("FOI");
    }
}
