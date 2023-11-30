package dev.greenhouseteam.rdrtestmod;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.arguments.ResourceOrTagArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;

public class TestCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
            LiteralCommandNode<CommandSourceStack> testNode = Commands
                    .literal("rdrtest")
                    .requires(c -> c.hasPermission(2))
                    .build();


            LiteralCommandNode<CommandSourceStack> compareBasicRecordNode = Commands
                    .literal("comparebasicrecord")
                    .then(Commands.argument("value", ResourceArgument.resource(context, RDRTestMod.BASIC_RECORD))
                            .then(Commands.argument("compareTo", ResourceOrTagArgument.resourceOrTag(context, RDRTestMod.BASIC_RECORD))
                                    .executes(TestCommand::checkTags)))
                    .build();

            dispatcher.getRoot().addChild(testNode);
            testNode.addChild(compareBasicRecordNode);
    }

    public static int checkTags(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Holder.Reference<BasicRecord> value = ResourceArgument.getResource(context, "value", RDRTestMod.BASIC_RECORD);
        ResourceOrTagArgument.Result<BasicRecord> compareTo = ResourceOrTagArgument.getResourceOrTag(context, "compareTo", RDRTestMod.BASIC_RECORD);

        HolderSet<BasicRecord> compareToHolderSet = compareTo.unwrap().map(HolderSet::direct, holders -> holders);

        boolean matches = compareToHolderSet.contains(value);

        if (matches)
            context.getSource().sendSuccess(() -> Component.literal("The value is/is in the specified comparison value."), true);
        else
            context.getSource().sendSuccess(() -> Component.literal("The value is not/is not in the specified comparison value."), true);

        return matches ? 1 : 0;
    }

}
