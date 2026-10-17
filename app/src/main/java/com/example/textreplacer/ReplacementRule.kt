package com.example.textreplacer

/**
 * 一条替换规则：把 [from] 替换为 [to]。
 */
data class ReplacementRule(
    val from: String,
    val to: String
)
