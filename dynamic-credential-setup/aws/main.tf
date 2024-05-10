resource "aws_iam_openid_connect_provider" "terrakube_provider" {
  url             = data.tls_certificate.terrakube_certificate.url
  client_id_list  = [var.terrakube_federated_credentials_audience]
  thumbprint_list = [data.tls_certificate.terrakube_certificate.certificates[0].sha1_fingerprint]
}

resource "aws_iam_role" "terrakube_role" {
  name = "terrakube-role"

  assume_role_policy = <<EOF
{
 "Version": "2012-10-17",
 "Statement": [
   {
     "Effect": "Allow",
     "Principal": {
       "Federated": "${aws_iam_openid_connect_provider.terrakube_provider.arn}"
     },
     "Action": "sts:AssumeRoleWithWebIdentity",
     "Condition": {
        "StringEquals": {
        "${var.terrakube_api_hostname}:aud": "${var.terrakube_federated_credentials_audience}",
        "${var.terrakube_api_hostname}:sub": "organization:${var.terrakube_organization_name}:workspace:${var.terrakube_workspace_name}"
        }
     }
   }
 ]
}
EOF
}

resource "aws_iam_policy" "terrakube_policy" {
  name        = "terrakube-policy"
  description = "terrakube policy"

  policy = <<EOF
{
 "Version": "2012-10-17",
 "Statement": [
   {
     "Effect": "Allow",
     "Action": [
       "s3:*"
     ],
     "Resource": "*"
   }
 ]
}
EOF
}

resource "aws_iam_role_policy_attachment" "terrakube_policy_attachment" {
  role       = aws_iam_role.terrakube_role.name
  policy_arn = aws_iam_policy.terrakube_policy.arn
}


resource "terrakube_workspace_cli" "dynamic_credentials_workspace" {
  organization_id = data.terrakube_organization.org.id
  name            = var.terrakube_workspace_name
  description     = "Sample description"
  execution_mode  = "remote"
  iac_type        = "terraform"
  iac_version     = "1.5.7"

  depends_on = [data.terrakube_organization.org]
}


resource "terrakube_workspace_variable" "env1" {
  organization_id = data.terrakube_organization.org.id
  workspace_id    = terrakube_workspace_cli.dynamic_credentials_workspace.id
  key             = "ENABLE_DYNAMIC_CREDENTIALS_AWS"
  value           = "true"
  description     = "ENABLE_DYNAMIC_CREDENTIALS_AWS"
  category        = "ENV"
  sensitive       = false
  hcl             = false
}


resource "terrakube_workspace_variable" "env2" {
  organization_id = data.terrakube_organization.org.id
  workspace_id    = terrakube_workspace_cli.dynamic_credentials_workspace.id
  key             = "WORKLOAD_IDENTITY_AUDIENCE_AWS"
  value           = var.terrakube_federated_credentials_audience
  description     = "WORKLOAD_IDENTITY_AUDIENCE_AWS"
  category        = "ENV"
  sensitive       = false
  hcl             = false
}


resource "terrakube_workspace_variable" "env3" {
  organization_id = data.terrakube_organization.org.id
  workspace_id    = terrakube_workspace_cli.dynamic_credentials_workspace.id
  key             = "WORKLOAD_IDENTITY_ROLE_AWS"
  value           = aws_iam_role.terrakube_role.arn
  description     = "WORKLOAD_IDENTITY_ROLE_AWS"
  category        = "ENV"
  sensitive       = false
  hcl             = false
}

resource "terrakube_workspace_variable" "env4" {
  organization_id = data.terrakube_organization.org.id
  workspace_id    = terrakube_workspace_cli.dynamic_credentials_workspace.id
  key             = "AWS_REGION"
  value           = var.aws_region
  description     = "WORKLOAD_IDENTITY_ROLE_AWS"
  category        = "ENV"
  sensitive       = false
  hcl             = false
}