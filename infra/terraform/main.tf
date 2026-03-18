terraform {
  required_version = ">= 1.0"

  required_providers {
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "~> 2.23"
    }
  }
}

provider "kubernetes" {
  config_path = var.kubeconfig_path
}

# Namespace
resource "kubernetes_namespace" "tech_challenge" {
  metadata {
    name = var.namespace
    labels = {
      app         = "tech-challenge"
      environment = var.environment
    }
  }
}

# ConfigMap
resource "kubernetes_config_map" "app_config" {
  metadata {
    name      = "tech-challenge-config"
    namespace = kubernetes_namespace.tech_challenge.metadata[0].name
  }

  data = {
    SPRING_PROFILES_ACTIVE                    = var.environment
    SPRING_DATASOURCE_URL                     = "jdbc:postgresql://${var.db_host}:${var.db_port}/${var.db_name}"
    SPRING_DATASOURCE_DRIVER_CLASS_NAME       = "org.postgresql.Driver"
    SPRING_JPA_HIBERNATE_DDL_AUTO             = "validate"
    LOGGING_LEVEL_ROOT                        = var.log_level
  }
}

# Secret
resource "kubernetes_secret" "app_secrets" {
  metadata {
    name      = "tech-challenge-secrets"
    namespace = kubernetes_namespace.tech_challenge.metadata[0].name
  }

  type = "Opaque"

  data = {
    SPRING_DATASOURCE_USERNAME = base64encode(var.db_user)
    SPRING_DATASOURCE_PASSWORD = base64encode(var.db_password)
    JWT_SECRET                 = base64encode(var.jwt_secret)
  }
}

# Service Account
resource "kubernetes_service_account" "app" {
  metadata {
    name      = "tech-challenge"
    namespace = kubernetes_namespace.tech_challenge.metadata[0].name
  }
}

# Deployment
resource "kubernetes_deployment" "app" {
  metadata {
    name      = "tech-challenge"
    namespace = kubernetes_namespace.tech_challenge.metadata[0].name
  }

  spec {
    replicas = var.app_replicas

    selector {
      match_labels = {
        app = "tech-challenge"
      }
    }

    template {
      metadata {
        labels = {
          app = "tech-challenge"
        }
      }

      spec {
        service_account_name = kubernetes_service_account.app.metadata[0].name

        container {
          image             = var.app_image
          image_pull_policy = "IfNotPresent"
          name              = "app"

          port {
            container_port = 8080
          }

          env_from {
            config_map_ref {
              name = kubernetes_config_map.app_config.metadata[0].name
            }
          }

          env_from {
            secret_ref {
              name = kubernetes_secret.app_secrets.metadata[0].name
            }
          }

          resources {
            requests = {
              cpu    = "200m"
              memory = "256Mi"
            }
            limits = {
              cpu    = "500m"
              memory = "512Mi"
            }
          }

          liveness_probe {
            http_get {
              path = "/actuator/health"
              port = 8080
            }
            initial_delay_seconds = 30
            period_seconds        = 10
          }

          readiness_probe {
            http_get {
              path = "/actuator/health"
              port = 8080
            }
            initial_delay_seconds = 10
            period_seconds        = 5
          }
        }
      }
    }
  }

  depends_on = [kubernetes_namespace.tech_challenge]
}

# Service
resource "kubernetes_service" "app" {
  metadata {
    name      = "tech-challenge"
    namespace = kubernetes_namespace.tech_challenge.metadata[0].name
  }

  spec {
    selector = {
      app = "tech-challenge"
    }

    port {
      port       = 8080
      target_port = 8080
    }

    type = var.service_type
  }

  depends_on = [kubernetes_deployment.app]
}

# HPA
resource "kubernetes_horizontal_pod_autoscaler_v2" "app" {
  metadata {
    name      = "tech-challenge-hpa"
    namespace = kubernetes_namespace.tech_challenge.metadata[0].name
  }

  spec {
    scale_target_ref {
      api_version = "apps/v1"
      kind        = "Deployment"
      name        = kubernetes_deployment.app.metadata[0].name
    }

    min_replicas = var.hpa_min_replicas
    max_replicas = var.hpa_max_replicas

    metric {
      type = "Resource"
      resource {
        name = "cpu"
        target {
          type                = "Utilization"
          average_utilization = 70
        }
      }
    }
  }

  depends_on = [kubernetes_deployment.app]
}

output "namespace" {
  value       = kubernetes_namespace.tech_challenge.metadata[0].name
}

output "service_name" {
  value       = kubernetes_service.app.metadata[0].name
}
