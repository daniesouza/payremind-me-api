package com.payremindme.api.mail;

import com.payremindme.api.model.Lancamento;
import com.payremindme.api.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class Mailer {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;


    public void enviarEmailLancamentosVencidos(List<Lancamento> vencidos, List<Usuario> usuarios) {

        Map<String, Object> params = new HashMap<>();
        params.put("lancamentos", vencidos);

        List<String> destinatarios = usuarios.stream().map(Usuario::getEmail).collect(Collectors.toList());

        enviarEmail("PayRemind-me",
                destinatarios,
                "Lancamentos Vencidos",
                "mail/aviso-lancamentos-vencidos",
                params);

    }

    private void enviarEmail(String remetente, List<String> destinatarios, String assunto, String template, Map<String, Object> params) {

        Context context = new Context(new Locale("pt", "BR"));
        params.forEach(context::setVariable);

        String mensagem = templateEngine.process(template, context);
        enviarEmail(remetente, destinatarios, assunto, mensagem);
    }

    private void enviarEmail(String remetente, List<String> destinatarios, String assunto, String mensagem) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, "UTF-8");

        try {
            messageHelper.setFrom(remetente);
            messageHelper.setTo(destinatarios.toArray(new String[0]));
            messageHelper.setSubject(assunto);
            messageHelper.setText(mensagem, true);

            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new RuntimeException("Falha ao enviar email", e);
        }
    }
}
