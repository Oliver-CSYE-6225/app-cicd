resource "aws_iam_policy" "CodeDeploy-EC2-S3" {
  name        = var.ec2_codedeploy_policy_name
  description = var.ec2_codedeploy_policy_name_description


  policy = jsonencode({
    "Version" : "2012-10-17",
    "Statement" : [
      {
        "Action" : [
          "s3:Get*",
          "s3:List*"
        ],
        "Effect" : "Allow",
        "Resource" : [
          var.artifact_s3_arn,
          format("%s%s", var.artifact_s3_arn, "/*")
        ]
    }]
  })
}

data "aws_iam_role" "s3_access" {
  name = "EC2-CSYE6225"
}

resource "aws_iam_role_policy_attachment" "attach_ec2-s3" {
  role       = data.aws_iam_role.s3_access.name
  policy_arn = aws_iam_policy.CodeDeploy-EC2-S3.arn
}

resource "aws_iam_policy" "GH-Upload-To-S3" {
  name        = var.s3_artifact_upload_policy_name
  description = var.s3_artifact_upload_description

  # Terraform's "jsonencode" function converts a
  # Terraform expression result to valid JSON syntax.
  policy = jsonencode({
    "Version" = "2012-10-17",
    "Statement" = [
      {
        "Effect" = "Allow",
        "Action" = [
          "s3:PutObject",
          "s3:Get*",
          "s3:List*"
        ],
        "Resource" = [
          var.artifact_s3_arn,
          format("%s%s", var.artifact_s3_arn, "/*")
        ]
      }
    ]
  })
}

resource "aws_iam_policy_attachment" "attach-s3-upload" {
  name       = "test-attachment"
  users      = ["ghactions-app"]
  policy_arn = aws_iam_policy.GH-Upload-To-S3.arn
}



resource "aws_iam_policy" "GH-Code-Deploy" {
  name        = var.ghactions_codedeploy_policy_name
  description = var.ghactions_codedeploy_policy_description

  # Terraform's "jsonencode" function converts a
  # Terraform expression result to valid JSON syntax.
  policy = jsonencode({
    "Version" : "2012-10-17",
    "Statement" : [
      {
        "Effect" : "Allow",
        "Action" : [
          "codedeploy:RegisterApplicationRevision",
          "codedeploy:GetApplicationRevision"
        ],
        "Resource" : [
          format("%s%s%s%s%s%s", "arn:aws:codedeploy:", var.aws_region, ":", var.aws_account_id, ":application:", var.codedeploy_application_name)
        ]
      },
      {
        "Effect" : "Allow",
        "Action" : [
          "codedeploy:CreateDeployment",
          "codedeploy:GetDeployment"
        ],
        "Resource" : [
          "arn:aws:codedeploy:${var.aws_region}:${var.aws_account_id}:deploymentgroup:${var.codedeploy_application_name}/${var.codedeploy_deployment_group}"
        ]
      },
      {
        "Effect" : "Allow",
        "Action" : [
          "codedeploy:GetDeploymentConfig"
        ],
        "Resource" : [
          "arn:aws:codedeploy:${var.aws_region}:${var.aws_account_id}:deploymentconfig:CodeDeployDefault.OneAtATime",
          "arn:aws:codedeploy:${var.aws_region}:${var.aws_account_id}:deploymentconfig:CodeDeployDefault.HalfAtATime",
          "arn:aws:codedeploy:${var.aws_region}:${var.aws_account_id}:deploymentconfig:CodeDeployDefault.AllAtOnce"
        ]
      }
    ]
  })
}

resource "aws_iam_policy_attachment" "attach-code-deploy" {
  name       = "test-attachment"
  users      = ["ghactions-app"]
  policy_arn = aws_iam_policy.GH-Code-Deploy.arn
}

resource "aws_iam_role" "gh_actions_role" {
  depends_on          = [aws_iam_policy.GH-Upload-To-S3, aws_iam_policy.GH-Code-Deploy]
  name                = var.gh_actions_role_name
  managed_policy_arns = [aws_iam_policy.GH-Upload-To-S3.arn, aws_iam_policy.GH-Code-Deploy.arn]
  assume_role_policy = jsonencode({
    "Version" : "2012-10-17",
    "Statement" : [
      {
        "Effect" : "Allow",
        "Principal" : { "AWS" : "arn:aws:iam::746774523931:user/ghactions-app" },
        "Action" : "sts:AssumeRole"
      }
    ]
  })
}

resource "aws_iam_role" "codedeploy_service_role" {
  name = "codedeploy-service-role"

  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "",
      "Effect": "Allow",
      "Principal": {
        "Service": [
          "codedeploy.amazonaws.com"
        ]
      },
      "Action": "sts:AssumeRole"
    }
  ]
}
EOF
}

resource "aws_iam_role_policy_attachment" "codedeploy_service_policy_attach" {
  role       =  aws_iam_role.codedeploy_service_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSCodeDeployRole"
}

resource "aws_codedeploy_app" "codedeploy_app" {
  compute_platform = "Server"
  name             = "csye6225-webapp"
}


resource "aws_codedeploy_deployment_group" "codedeploy_group" {
  depends_on = [aws_codedeploy_app.codedeploy_app]
  app_name               = "csye6225-webapp"
  deployment_group_name = "csye6225-webapp-deployment"
  service_role_arn       = aws_iam_role.codedeploy_service_role.arn
  deployment_config_name = "CodeDeployDefault.AllAtOnce"
  deployment_style {
    deployment_type = "IN_PLACE"
  }

  auto_rollback_configuration {
    enabled = true
    events  = ["DEPLOYMENT_FAILURE"]
  }

  ec2_tag_filter {
    key = "instance_identifier"
    type = "KEY_AND_VALUE"
    value = "webapp_deploy"
  }
}

data "aws_route53_zone" "selected_zone" {
  name         = var.zone_name
}

data "aws_instance" "ec2_instance" {

  filter {
    name   = "tag:Name"
    values = ["csye-6225-1"]
  }
}

resource "aws_route53_record" "webapp_record" {
  zone_id = data.aws_route53_zone.selected_zone.zone_id
  name    = var.domain_name
  type    = "A"
  ttl     = "60"
  records = [data.aws_instance.ec2_instance.public_ip]
}