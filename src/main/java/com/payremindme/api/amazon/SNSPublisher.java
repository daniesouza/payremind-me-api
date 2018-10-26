package com.payremindme.api.amazon;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.payremindme.api.model.Lancamento;
import com.payremindme.api.model.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SNSPublisher{

    private static final Logger logger = LoggerFactory.getLogger(SNSPublisher.class);


    private AmazonSNS amazonSNS;

    private String snsTopicInterviewStatusARN = "arn:aws:sns:sa-east-1:162724216324:payremind-me-sns";

    public static final String TOPIC_INTERVIEWSTATUS = "payremind-me-sns";

    @Autowired
    public SNSPublisher(AmazonSNS amazonSNS){
        this.amazonSNS = amazonSNS;
    }

    public void enviarSMSLancamentosVencidos(List<Lancamento> vencidos, List<Usuario> usuarios) {

        Map<String, Object> params = new HashMap<>();
        params.put("lancamentos", vencidos);

        List<String> telefones = usuarios.stream().map(Usuario::getTelefone).collect(Collectors.toList());

        String message = "PayRemind-me\n Lancamentos Vencidos. \n Olá, Existem "+vencidos.size()+" lancamentos vencidos";

        telefones.forEach(telefone -> publish(message,telefone));

    }

    private void publish(String message,String phoneNumber) {


//        String message = "Hello World";
//        String phoneNumber = "+5511983916213";

//        Map<String, MessageAttributeValue> smsAttributes =
//                new HashMap<String, MessageAttributeValue>();
//        smsAttributes.put("AWS.SNS.SMS.SenderID", new MessageAttributeValue()
//                .withStringValue("mySenderID") //The sender ID shown on the device.
//                .withDataType("String"));
//        smsAttributes.put("AWS.SNS.SMS.MaxPrice", new MessageAttributeValue()
//                .withStringValue("0.50") //Sets the max price to 0.50 USD.
//                .withDataType("Number"));
//        smsAttributes.put("AWS.SNS.SMS.SMSType", new MessageAttributeValue()
//                .withStringValue("Promotional") //Sets the type to promotional.
//                .withDataType("String"));

        Map<String, MessageAttributeValue> smsAttributes =  new HashMap<>();


        PublishResult publishResult = amazonSNS.publish(new PublishRequest()
                .withMessage(message)
                .withPhoneNumber(phoneNumber)
                .withMessageAttributes(smsAttributes));
        System.out.println(publishResult); // Prints the message ID.

        logger.info(publishResult.toString());
        logger.info("MessageId - " + publishResult.getMessageId());

    }

}