package net.beholderface.hexpsi.hex.actions

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import net.beholderface.hexpsi.hex.environment.TrinketBulletCastEnv
import vazkii.psi.api.spell.SpellContext
import java.util.function.Function

class OpReadPsiContextData(val getter : Function<SpellContext, Iota>) : ConstMediaAction {
    override val argc = 0
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        if (env is TrinketBulletCastEnv){
            return listOf(getter.apply(env.psiContext))
        } else {
            //TODO: make bespoke mishap
            throw MishapBadCaster()
        }
    }
}