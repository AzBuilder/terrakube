output "aws_identity_role" {
  value = aws_iam_role.terrakube_role.arn
}

output "aws_audience" {
  value = var.terrakube_federated_credentials_audience
}