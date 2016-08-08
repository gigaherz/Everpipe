package gigaherz.everpipe.pipe;

import gigaherz.everpipe.Everpipe;
import gigaherz.everpipe.common.BlockRegistered;
import gigaherz.everpipe.pipe.connectors.Connector;
import gigaherz.everpipe.pipe.connectors.ConnectorHandler;
import gigaherz.everpipe.pipe.connectors.ConnectorStateData;
import gigaherz.everpipe.pipe.connectors.items.ItemHandlerConnector;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import javax.annotation.Nullable;

public class BlockPipe extends BlockRegistered
{
    public static final AxisAlignedBB AABB = new AxisAlignedBB(2/16.0f,2/16.0f,2/16.0f,14/16.0f,14/16.0f,14/16.0f);

    public static final PropertyBool NORTH = PropertyBool.create("north");
    public static final PropertyBool SOUTH = PropertyBool.create("south");
    public static final PropertyBool WEST = PropertyBool.create("west");
    public static final PropertyBool EAST = PropertyBool.create("east");
    public static final PropertyBool UP = PropertyBool.create("up");
    public static final PropertyBool DOWN = PropertyBool.create("down");

    public static final IUnlistedProperty<ConnectorStateData> CONNECTORS = new IUnlistedProperty<ConnectorStateData>()
    {
        public String getName() { return Everpipe.location("connectors_property").toString(); }
        public boolean isValid(ConnectorStateData state) { return true; }
        public Class<ConnectorStateData> getType() { return ConnectorStateData.class; }
        public String valueToString(ConnectorStateData state) { return state.toString(); }
    };

    public BlockPipe(String name)
    {
        super(name, Material.IRON, MapColor.GRAY);
        setSoundType(SoundType.METAL);
        setCreativeTab(Everpipe.tabEverpipe);
        setDefaultState(blockState.getBaseState()
                .withProperty(NORTH, false)
                .withProperty(SOUTH, false)
                .withProperty(WEST, false)
                .withProperty(EAST, false)
                .withProperty(UP, false)
                .withProperty(DOWN, false));
        setHardness(3.0F);
        setResistance(8.0F);
    }

    @Deprecated
    @Override
    public boolean isFullBlock(IBlockState state)
    {
        return false;
    }

    @Deprecated
    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Deprecated
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        double nx = AABB.minX;
        double ny = AABB.minY;
        double nz = AABB.minZ;
        double mx = AABB.maxX;
        double my = AABB.maxY;
        double mz = AABB.maxZ;
        state = getActualState(state, source, pos);
        if (state.getValue(NORTH)) nz = 0;
        if (state.getValue(SOUTH)) mz = 1;
        if (state.getValue(WEST)) nx = 0;
        if (state.getValue(EAST)) mx = 1;
        if (state.getValue(DOWN)) ny = 0;
        if (state.getValue(UP)) my = 1;
        return new AxisAlignedBB(nx, ny, nz, mx, my, mz);
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Override
    public TilePipe createTileEntity(World world, IBlockState state)
    {
        return new TilePipe();
    }

    @Deprecated
    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return 0;
    }

    @Deprecated
    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState();
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        IExtendedBlockState augmented = (IExtendedBlockState)super.getExtendedState(state, world, pos);
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TilePipe)
        {
            TilePipe pipe = (TilePipe)te;

            augmented = augmented.withProperty(CONNECTORS, pipe.getConnectors());
        }

        return augmented;
    }

    @Deprecated
    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        return state
                .withProperty(NORTH, isConnectable(worldIn, pos, EnumFacing.NORTH))
                .withProperty(SOUTH, isConnectable(worldIn, pos, EnumFacing.SOUTH))
                .withProperty(WEST, isConnectable(worldIn, pos, EnumFacing.WEST))
                .withProperty(EAST, isConnectable(worldIn, pos, EnumFacing.EAST))
                .withProperty(UP, isConnectable(worldIn, pos, EnumFacing.UP))
                .withProperty(DOWN, isConnectable(worldIn, pos, EnumFacing.DOWN));
    }

    private boolean isConnectable(IBlockAccess worldIn, BlockPos pos, EnumFacing facing)
    {
        TileEntity te = worldIn.getTileEntity(pos.offset(facing));

        return (te instanceof TilePipe);
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new ExtendedBlockState(this, new IProperty[]{NORTH, SOUTH, WEST, EAST, UP, DOWN}, new IUnlistedProperty[]{CONNECTORS});
    }

    @Deprecated
    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn)
    {
        super.neighborChanged(state, worldIn, pos, blockIn);
        TileEntity te = worldIn.getTileEntity(pos);
        if (te != null)
            te.markDirty();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof TilePipe)
        {
            TilePipe pipe = (TilePipe)te;

            if (pipe.addConnector(side, ConnectorHandler.REGISTRY.getValue(ItemHandlerConnector.KEY).createInstance()))
            {
                worldIn.notifyBlockUpdate(pos, state, state, 3);
            }

            return true;
        }
        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, heldItem, side, hitX, hitY, hitZ);
    }

    @Override
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor)
    {
        super.onNeighborChange(world, pos, neighbor);
        if (isUpdateSource(world, pos, fromNeighbour(pos, neighbor)))
            ((TilePipe) world.getTileEntity(pos)).broadcastDirty();
    }

    private boolean isUpdateSource(IBlockAccess worldIn, BlockPos pos, EnumFacing facing)
    {
        //TileEntity te = worldIn.getTileEntity(pos.offset(facing));
        return false; //isAutomatable(te, facing.getOpposite());
    }

    private EnumFacing fromNeighbour(BlockPos a, BlockPos b)
    {
        BlockPos diff = b.subtract(a);
        return EnumFacing.getFacingFromVector(diff.getX(), diff.getY(), diff.getZ());
    }
}
