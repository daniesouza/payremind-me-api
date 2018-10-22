package com.payremindme.api.repository.listener;

import com.payremindme.api.PayremindMeApiApplication;
import com.payremindme.api.model.Lancamento;
import com.payremindme.api.storage.StorageS3;
import org.springframework.util.StringUtils;

import javax.persistence.PostLoad;

public class LancamentoAnexoListener {

    @PostLoad
    public void postLoad(Lancamento lancamento){

        if(StringUtils.hasText(lancamento.getAnexo())){
            StorageS3 storageS3 = PayremindMeApiApplication.getBean(StorageS3.class);
            lancamento.setUrlAnexo(storageS3.configurarURL(lancamento.getAnexo()));
        }
    }
}
