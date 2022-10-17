package com.example.lv0player.model;

import java.util.List;

public class MusicDetail {

    private List<SongsDTO> songs;
    private List<PrivilegesDTO> privileges;
    private Integer code;

    public static class SongsDTO {
        private String name;
        private Integer id;
        private Integer pst;
        private Integer t;
        private List<ArDTO> ar;
        private List<?> alia;
        private Integer pop;
        private Integer st;
        private String rt;
        private Integer fee;
        
        private Integer v;
        
        private Object crbt;
        
        private String cf;
        
        private SongsDTO.AlDTO al;
        private Integer dt;
        
        private SongsDTO.HDTO h;
        
        private SongsDTO.MDTO m;
        
        private SongsDTO.LDTO l;
        
        private SongsDTO.SqDTO sq;
        
        private Object hr;
        
        private Object a;
        
        private String cd;
        
        private Integer no;
        
        private Object rtUrl;
        
        private Integer ftype;
        
        private List<?> rtUrls;
        
        private Integer djId;
        
        private Integer copyright;
        private Integer sId;
        
        private Integer mark;
        
        private Integer originCoverType;
        
        private Object originSongSimpleData;
        
        private Object tagPicList;
        
        private Boolean resourceState;
        
        private Integer version;
        
        private Object songJumpInfo;
        
        private Object entertainmentTags;
        
        private Object awardTags;
        
        private Integer single;
        
        private Object noCopyrightRcmd;
        
        private Integer mv;
        
        private Integer mst;
        
        private Integer cp;
        
        private Integer rtype;
        
        private Object rurl;
        
        private Long publishTime;
        
        private List<String> tns;

        public static class AlDTO {
            
            private Integer id;
            
            private String name;
            
            private String picUrl;
            
            private List<?> tns;
            private String picStr;
            
            private Long pic;

            public String getPicUrl(){
                return picUrl;
            }
        }

        public static class HDTO {
            
            private Integer br;
            
            private Integer fid;
            
            private Integer size;
            
            private Integer vd;
            
            private Integer sr;
        }

        public static class MDTO {
            
            private Integer br;
            
            private Integer fid;
            
            private Integer size;
            
            private Integer vd;
            
            private Integer sr;
        }

        public static class LDTO {
            
            private Integer br;
            
            private Integer fid;
            
            private Integer size;
            
            private Integer vd;
            
            private Integer sr;
        }

        public static class SqDTO {
            
            private Integer br;
            
            private Integer fid;
            
            private Integer size;
            
            private Integer vd;
            
            private Integer sr;
        }

        public static class ArDTO {
            
            private Integer id;
            
            private String name;
            
            private List<?> tns;
            
            private List<?> alias;
        }

        public AlDTO getAl(){
            return al;
        }
    }

    public static class PrivilegesDTO {
        
        private Integer id;
        
        private Integer fee;
        
        private Integer payed;
        
        private Integer st;
        
        private Integer pl;
        
        private Integer dl;
        
        private Integer sp;
        
        private Integer cp;
        
        private Integer subp;
        
        private Boolean cs;
        
        private Integer maxbr;
        
        private Integer fl;
        
        private Boolean toast;
        
        private Integer flag;
        
        private Boolean preSell;
        
        private Integer playMaxbr;
        
        private Integer downloadMaxbr;
        
        private String maxBrLevel;
        
        private String playMaxBrLevel;
        
        private String downloadMaxBrLevel;
        
        private String plLevel;
        
        private String dlLevel;
        
        private String flLevel;
        
        private Object rscl;
        
        private PrivilegesDTO.FreeTrialPrivilegeDTO freeTrialPrivilege;
        
        private List<ChargeInfoListDTO> chargeInfoList;

        public static class FreeTrialPrivilegeDTO {
            
            private Boolean resConsumable;
            
            private Boolean userConsumable;
            
            private Object listenType;
        }

        public static class ChargeInfoListDTO {
            
            private Integer rate;
            
            private Object chargeUrl;
            
            private Object chargeMessage;
            private Integer chargeType;
        }
    }

    public SongsDTO getSong(){
        return songs.get(0);
    }
}
