package com.creative.share.apps.homecare.models;


import java.io.Serializable;
import java.util.List;

public class ServicesDataModel implements Serializable {
    private List<ServiceModel> services;

    public List<ServiceModel> getServices() {
        return services;
    }

    public static class  ServiceModel implements Serializable {
        private String service_id;
        private String logo;
        private WordsModel words;

        public WordsModel getWords() {
            return words;
        }

        public String getService_id() {
            return service_id;
        }

        public String getLogo() {
            return logo;
        }

        public void setWords(WordsModel words) {
            this.words = words;
        }

        public static class WordsModel implements Serializable {
            private String title;

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }
        }
    }


}
