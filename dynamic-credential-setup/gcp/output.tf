
output "audience" {
  value = "//iam.googleapis.com/projects/${data.google_project.project.number}/locations/global/workloadIdentityPools/${google_iam_workload_identity_pool.terrakube_pool.workload_identity_pool_id}/providers/${google_iam_workload_identity_pool_provider.terrakube_provider.workload_identity_pool_provider_id}"
}

output "service_account_email" {
  value = google_service_account.terrakube_service_account.email
}

output "terrakube_org_id" {
  value = data.terrakube_organization.org.id
}