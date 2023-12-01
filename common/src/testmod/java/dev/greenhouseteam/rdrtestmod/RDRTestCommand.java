package dev.greenhouseteam.rdrtestmod;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.greenhouseteam.rdrtestmod.record.BasicRecord;
import dev.greenhouseteam.rdrtestmod.record.Chocolate;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.arguments.ResourceOrTagArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.network.chat.Component;

import java.util.Locale;

public class RDRTestCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        LiteralCommandNode<CommandSourceStack> testNode = Commands
                .literal("rdr")
                .requires(c -> c.hasPermission(2))
                .build();


        LiteralCommandNode<CommandSourceStack> compareBasicRecordNode = Commands
                .literal("comparebasicrecord")
                .then(Commands.argument("value", ResourceArgument.resource(context, TestModReloadableRegistries.BASIC_RECORD))
                        .then(Commands.argument("compareTo", ResourceOrTagArgument.resourceOrTag(context, TestModReloadableRegistries.BASIC_RECORD))
                                .executes(RDRTestCommand::checkTags)))
                .build();

        LiteralCommandNode<CommandSourceStack> colorNode = Commands
                .literal("color")
                .then(Commands.argument("value", ResourceArgument.resource(context, TestModReloadableRegistries.BASIC_RECORD))
                        .executes(RDRTestCommand::color))
                .build();

        LiteralCommandNode<CommandSourceStack> entityTypeNode = Commands
                .literal("entitytype")
                .then(Commands.argument("value", ResourceArgument.resource(context, TestModReloadableRegistries.BASIC_RECORD))
                        .executes(RDRTestCommand::entityType))
                .build();

        LiteralCommandNode<CommandSourceStack> favoriteChocolateNode = Commands
                .literal("chocolate")
                .then(Commands.argument("value", ResourceArgument.resource(context, TestModReloadableRegistries.BASIC_RECORD))
                        .executes(RDRTestCommand::favoriteChocolate))
                .build();

        dispatcher.getRoot().addChild(testNode);
        testNode.addChild(colorNode);
        testNode.addChild(entityTypeNode);
        testNode.addChild(compareBasicRecordNode);
        testNode.addChild(favoriteChocolateNode);
    }

    private static int checkTags(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Holder.Reference<BasicRecord> value = ResourceArgument.getResource(context, "value", TestModReloadableRegistries.BASIC_RECORD);
        ResourceOrTagArgument.Result<BasicRecord> compareTo = ResourceOrTagArgument.getResourceOrTag(context, "compareTo", TestModReloadableRegistries.BASIC_RECORD);

        HolderSet<BasicRecord> compareToHolderSet = compareTo.unwrap().map(HolderSet::direct, holders -> holders);

        boolean matches = compareToHolderSet.contains(value);

        if (matches)
            compareTo.unwrap()
                    .ifLeft(basicRecordReference ->  context.getSource().sendSuccess(() -> Component.literal("'" + value.key().location() + "' and " + basicRecordReference.key().location() + " are one in the same."), true))
                    .ifRight(holders -> context.getSource().sendSuccess(() -> Component.literal("'" + value.key().location() + "' is in the specified tag '" + holders.key().location() + "'."), true));
        else
            compareTo.unwrap()
                    .ifLeft(basicRecordReference ->  context.getSource().sendSuccess(() -> Component.literal("'" + value.key().location() + "' and " + basicRecordReference.key().location() + " are not one in the same."), true))
                    .ifRight(holders -> context.getSource().sendSuccess(() -> Component.literal("'" + value.key().location() + "' is not in the specified tag '" + holders.key().location() + "'."), true));

        return matches ? 1 : 0;
    }

    private static int color(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Holder.Reference<BasicRecord> value = ResourceArgument.getResource(context, "value", TestModReloadableRegistries.BASIC_RECORD);

        if (!value.isBound()) {
            context.getSource().sendFailure(Component.literal("Basic record value '" + value.key().location() + "' is not bound."));
            return 0;
        }

        context.getSource().sendSuccess(() -> Component.literal(value.key().location() + "'s associated color is '" + value.value().color() + "'."), true);

        return 0;
    }

    private static int entityType(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Holder.Reference<BasicRecord> value = ResourceArgument.getResource(context, "value", TestModReloadableRegistries.BASIC_RECORD);

        if (!value.isBound()) {
            context.getSource().sendFailure(Component.literal("Basic record value '" + value.key().location() + "' is not bound."));
            return 0;
        }

        if (value.value().entityType().unwrapKey().isEmpty()) {
            context.getSource().sendFailure(Component.literal("Basic record value's entity type is empty."));
            return 0;
        }

        context.getSource().sendSuccess(() -> Component.literal(value.key().location() + "'s associated entity type is '" + value.value().entityType().unwrapKey().get().location() + "'."), true);

        return 0;
    }

    private static int favoriteChocolate(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Holder.Reference<BasicRecord> value = ResourceArgument.getResource(context, "value", TestModReloadableRegistries.BASIC_RECORD);

        if (!value.isBound()) {
            context.getSource().sendFailure(Component.literal("Basic record value '" + value.key().location() + "' is not bound."));
            return 0;
        }

        BasicRecord basicRecord = value.value();
        if (basicRecord.favoriteChocolate().isPresent() && basicRecord.favoriteChocolate().get().isBound()) {
            Chocolate favoriteChocolate = basicRecord.favoriteChocolate().get().value();
            String chocolateName = favoriteChocolate.chocolateType().name().charAt(0) + favoriteChocolate.chocolateType().name().toLowerCase(Locale.ROOT).substring(1);
            String nutsMessage = favoriteChocolate.nutType().isPresent() ? "they like " + favoriteChocolate.nutType().get().plural().charAt(0) + favoriteChocolate.nutType().get().plural().toLowerCase(Locale.ROOT).substring(1) + " inside." : "does not prefer nuts.";
            context.getSource().sendSuccess(() -> Component.literal(value.key().location() + "'s favorite chocolate is " + chocolateName + ", and " + nutsMessage), true);
        }
        else
            context.getSource().sendSuccess(() -> Component.literal(value.key().location() + " does not have a favorite chocolate. :("), true);

        return 0;
    }

}
