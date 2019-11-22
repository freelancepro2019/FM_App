package com.creative.share.apps.homecare.models;


import java.io.Serializable;
import java.util.List;

public class SubServicesModel implements Serializable {
    private List<SubServiceModel> services;

    public class SubServiceModel implements Serializable {
        private int id;
        private String logo;
        private WordsModel words;

        public WordsModel getWords() {
            return words;
        }

        public int getId() {
            return id;
        }

        public String getLogo() {
            return logo;
        }

        public class WordsModel implements Serializable {
            private String title;
            private String content;

            public String getTitle() {
                return title;
            }

            public String getContent() {
                return content;
            }
        }
    }

    public List<SubServiceModel> getServices() {
        return services;
    }

}
