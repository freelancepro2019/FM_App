package com.day.star.apps.homecare.models;


import java.io.Serializable;
import java.util.List;

public class SubServicesModel implements Serializable {
    private List<SubServiceModel> services;

    public class SubServiceModel implements Serializable {
        private String service_id;
        private String logo;
        private String cost;
        private WordsModel words;

        public WordsModel getWords() {
            return words;
        }

        public String getId() {
            return service_id;
        }

        public String getLogo() {
            return logo;
        }

        public String getCost() {
            return cost;
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
