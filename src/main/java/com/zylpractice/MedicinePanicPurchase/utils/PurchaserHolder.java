package com.zylpractice.MedicinePanicPurchase.utils;

import com.zylpractice.MedicinePanicPurchase.dto.PurchaserDTO;

public class PurchaserHolder {
    private static final ThreadLocal<PurchaserDTO> tl = new ThreadLocal<>();

    public static void savePurchaser(PurchaserDTO purchaser){
        tl.set(purchaser);
    }

    public static PurchaserDTO getPurchaser(){
        return tl.get();
    }

    public static void removePurchaser(){
        tl.remove();
    }
}
