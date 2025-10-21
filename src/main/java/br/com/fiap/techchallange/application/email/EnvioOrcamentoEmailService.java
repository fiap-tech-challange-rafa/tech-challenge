package br.com.fiap.techchallange.application.email;

import br.com.fiap.techchallange.domain.cliente.Cliente;
import br.com.fiap.techchallange.domain.ordemservico.OrdemServico;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EnvioOrcamentoEmailService {

    private final JavaMailSender mailSender;

    public EnvioOrcamentoEmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarOrcamento(Cliente cliente, OrdemServico os) throws MessagingException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(cliente.getEmail().getValor());
            helper.setSubject("Orçamento da Ordem de Serviço #" + os.getId());

            String linkAprovar = "http://localhost:8080/api/ordem-servico/" + os.getId() + "/aprovar";

            String conteudo = "<h3>Olá " + cliente.getNome() + ",</h3>" +
                    "<p>Segue o orçamento da sua ordem de serviço:</p>" +
                    "<ul>" +
                    "<li>Total: R$ " + os.getTotalOrcamento() + "</li>" +
                    "</ul>" +
                    "<p>Clique no botão abaixo para <b>aprovar o orçamento</b>:</p>" +
                    "<a href='" + linkAprovar + "' style='padding:10px 20px; background-color:green; color:white; text-decoration:none;'>Aprovar Orçamento</a>";

            helper.setText(conteudo, true);

            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao enviar e-mail");
        }
    }
}
