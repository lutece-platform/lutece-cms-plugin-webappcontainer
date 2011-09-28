DROP TABLE IF EXISTS webappcontainer_site;

--
-- Structure for table webappcontainer_site
--


CREATE TABLE webappcontainer_site (
  site_code VARCHAR(255) NOT NULL,
  site_url long varchar NOT NULL,
  site_description long varchar NOT NULL,
  site_encoding long varchar NOT NULL,
  site_hat long varchar,
  site_workgroup_key VARCHAR(50) NOT NULL,
  site_rebuild_html_page smallint NOT NULL,
  site_redirect_non_html_content smallint NOT NULL,
  site_use_proxy smallint NOT NULL,
  PRIMARY KEY (site_code)
);
