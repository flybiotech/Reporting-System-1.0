package com.shizhenbao.pop;

import org.litepal.crud.DataSupport;

/**
 * Created by zhangbin on 2017/5/26.
 */

public class Diacrisis extends DataSupport {
    private int diaId;
    private String cytology;//细胞学
    private String dna;//dna
    private String Symptom;//症状
    private String Assessment;//评估
    private String region;//区域
    private String Colposcopy;//阴道镜所见
    private String Suspected;//拟诊
    private String attention;//注意事项
    private String bianjie;//边界/表面
    private String color;//颜色
    private String xueguna;//血管
    private String dianranse;//碘染色
    private String cusuan;//醋酸
    private String handle;//注意事项

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getBianjie() {
        return bianjie;
    }

    public void setBianjie(String bianjie) {
        this.bianjie = bianjie;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getXueguna() {
        return xueguna;
    }

    public void setXueguna(String xueguna) {
        this.xueguna = xueguna;
    }

    public String getDianranse() {
        return dianranse;
    }

    public void setDianranse(String dianranse) {
        this.dianranse = dianranse;
    }

    public String getCusuan() {
        return cusuan;
    }

    public void setCusuan(String cusuan) {
        this.cusuan = cusuan;
    }

    public int getDiaId() {
        return diaId;
    }

    public void setDiaId(int diaId) {
        this.diaId = diaId;
    }

    public String getCytology() {
        return cytology;
    }

    public void setCytology(String cytology) {
        this.cytology = cytology;
    }

    public String getDna() {
        return dna;
    }

    public void setDna(String dna) {
        this.dna = dna;
    }

    public String getSymptom() {
        return Symptom;
    }

    public void setSymptom(String symptom) {
        Symptom = symptom;
    }

    public String getAssessment() {
        return Assessment;
    }

    public void setAssessment(String assessment) {
        Assessment = assessment;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getColposcopy() {
        return Colposcopy;
    }

    public void setColposcopy(String colposcopy) {
        Colposcopy = colposcopy;
    }

    public String getSuspected() {
        return Suspected;
    }

    public void setSuspected(String suspected) {
        Suspected = suspected;
    }

    public String getAttention() {
        return attention;
    }

    public void setAttention(String attention) {
        this.attention = attention;
    }
}
