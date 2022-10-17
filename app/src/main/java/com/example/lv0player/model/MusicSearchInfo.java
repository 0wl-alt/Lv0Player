package com.example.lv0player.model;

import java.util.List;

public class MusicSearchInfo {

    public ResultDTO result;
    public Integer code;

    public static class ResultDTO {
        private List<SongsDTO> songs;
        private Boolean hasMore;
        private Integer songCount;

        public static class SongsDTO {
            private Long id;
            private String name;
            private List<ArtistsDTO> artists;
            private ResultDTO.SongsDTO.AlbumDTO album;
            private Long duration;
            private Long copyrightId;
            private Long status;
            private List<?> alias;
            private Long rtype;
            private Long ftype;
            private List<String> transNames;
            private Long mvid;
            private Long fee;
            private Object rUrl;
            private Long mark;

            public static class AlbumDTO {
                private Long id;
                private String name;
                private ResultDTO.SongsDTO.AlbumDTO.ArtistDTO artist;
                private Long publishTime;
                private Long size;
                private Long copyrightId;
                private Long status;
                private Long picId;
                private Long mark;

                public static class ArtistDTO {
                    private Long id;
                    private String name;
                    private Object picUrl;
                    private List<?> alias;
                    private Long albumSize;
                    private Long picId;
                    private Object fansGroup;
                    private String img1v1Url;
                    private Long img1v1;
                    private Object trans;

                    public String getImg1v1Url() {
                        return img1v1Url;
                    }
                }

                public ArtistDTO getArtist() {
                    return artist;
                }
            }

            public static class ArtistsDTO {
                private Long id;
                private String name;
                private Object picUrl;
                private List<?> alias;
                private Long albumSize;
                private Long picId;
                private Object fansGroup;
                private String img1v1Url;
                private Long img1v1;
                private Object trans;

                public String getName() {
                    return name;
                }
            }

            public Long getId() {
                return id;
            }
            public String getName() {
                return name;
            }
            public Long getDuration() {
                return duration;
            }

            public List<ArtistsDTO> getArtists() {
                return artists;
            }

            public void setName(String name) {
                this.name = name;
            }
        }

        public List<SongsDTO> getSongs() {
            return songs;
        }
    }

    public ResultDTO getResult() {
        return result;
    }
}
