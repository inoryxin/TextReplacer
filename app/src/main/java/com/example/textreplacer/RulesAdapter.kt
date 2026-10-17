package com.example.textreplacer

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView

class RulesAdapter : RecyclerView.Adapter<RulesAdapter.VH>() {

    private val rules = mutableListOf<ReplacementRule>()

    fun submit(list: List<ReplacementRule>) {
        rules.clear()
        rules.addAll(list)
        notifyDataSetChanged()
    }

    fun addRule() {
        rules.add(ReplacementRule("", ""))
        notifyItemInserted(rules.size - 1)
    }

    /** 返回所有有效规则（原文本非空）。 */
    fun getRules(): List<ReplacementRule> = rules.filter { it.from.isNotBlank() }.toList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_rule, parent, false)
        return VH(v)
    }

    override fun getItemCount(): Int = rules.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val rule = rules[position]

        holder.from.removeTextChangedListener(holder.fromWatcher)
        holder.to.removeTextChangedListener(holder.toWatcher)
        holder.from.setText(rule.from)
        holder.to.setText(rule.to)
        holder.from.tag = position
        holder.to.tag = position
        holder.from.addTextChangedListener(holder.fromWatcher)
        holder.to.addTextChangedListener(holder.toWatcher)

        holder.delete.setOnClickListener {
            val pos = holder.bindingAdapterPosition
            if (pos in 0 until rules.size) {
                rules.removeAt(pos)
                notifyItemRemoved(pos)
                notifyItemRangeChanged(pos, rules.size - pos)
            }
        }
    }

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val from: EditText = view.findViewById(R.id.fromEdit)
        val to: EditText = view.findViewById(R.id.toEdit)
        val delete: ImageButton = view.findViewById(R.id.deleteBtn)

        val fromWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
            override fun onTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val pos = from.tag as? Int ?: return
                if (pos in 0 until rules.size) {
                    rules[pos] = rules[pos].copy(from = s?.toString() ?: "")
                }
            }
        }

        val toWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
            override fun onTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val pos = to.tag as? Int ?: return
                if (pos in 0 until rules.size) {
                    rules[pos] = rules[pos].copy(to = s?.toString() ?: "")
                }
            }
        }
    }
}
