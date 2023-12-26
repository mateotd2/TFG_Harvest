package com.udc.fic.services;

import com.udc.fic.services.exceptions.CampaignAlreadyStartedException;

public interface CampanhaService {

    void comenzarCampanha() throws CampaignAlreadyStartedException;
}
