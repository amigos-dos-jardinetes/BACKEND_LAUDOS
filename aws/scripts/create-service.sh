#!/bin/bash
aws ecs create-service \
        --cluster arn:aws:ecs:sa-east-1:279634266618:cluster/utfpr-ct-cogeti-ecs-graviton \
        --service-name comissao-laudos-ct-backend \
        --task-definition arn:aws:ecs:sa-east-1:279634266618:task-definition/comissao-laudos-ct-backend:1 \
        --load-balancers "targetGroupArn=arn:aws:elasticloadbalancing:sa-east-1:279634266618:targetgroup/comissao-laudos-backend/1226c44275a1af29,containerName=comissao-laudos-ct,containerPort=8080" \
        --desired-count 1 \
        --availability-zone-rebalancing ENABLED \
        --scheduling-strategy REPLICA
