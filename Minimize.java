package chess.players.ai;

import chess.board.ChessBoardState;
import chess.moves.ChessMove;
import chess.players.ChessPlayerCPU;
import game.utility.Properties;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
//import static org.jocl.CL.*;
//import org.jocl.*;

public class Minimize extends Thread implements Callable<Integer>  {
    long            time;
    int             depth;
    int             alpha;
    int             beta;
    int             level;
    Properties.PlayerColor  playerColor;
    ChessBoardState         boardState;
    
    public int maximize_thread(long time,int depth, int alpha, int beta, int level, Properties.PlayerColor playerColor, ChessBoardState boardState){
        Callable<Integer> getScoreTask = new Maximize(time,depth+1,alpha,beta,level,playerColor,boardState);
        int score = 0;
        try {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            Future<Integer> fscore = executorService.submit(getScoreTask);
            score = fscore.get();
            executorService.shutdown();
            executorService.awaitTermination(100, TimeUnit.MILLISECONDS);
        } catch (Exception e) {

        }
        return score;
    }
    
    @Override
    public Integer call()
    {
        if (depth > ChessAI.minDepth[level]) {
            if (depth <= ChessAI.maxDepth[level] && ThreadLocalRandom.current().nextDouble() < ChessAI.pBoost[level])
            {
                
            }
            else
            {
                return ChessAI.evaluate(boardState,Properties.oppositeColor(playerColor));
            }
        }
        if (System.currentTimeMillis() - time > ChessAI.maxTime[level])
        {
            return ChessAI.evaluate(ChessBoardState.copy(boardState),Properties.oppositeColor(playerColor));
        }

        List<ChessMove> validMoves = ChessPlayerCPU.getValidMoves(boardState,playerColor);
        Collections.shuffle(validMoves);
        for (ChessMove move : validMoves)
        {
            if (System.currentTimeMillis() - time > ChessAI.maxTime[level])
            {
                return ChessAI.evaluate(ChessBoardState.copy(boardState),Properties.oppositeColor(playerColor));
            }
            ChessBoardState t_boardState = ChessBoardState.copy(boardState);
            move.commitMove(t_boardState);
            
            try {
                int score = maximize_thread(time,depth+1,alpha,beta,level,Properties.oppositeColor(playerColor),t_boardState);

                if (Math.random() > 0.5)
                {
                    if (score <= alpha){
                        return alpha;
                    }
                }
                else if (score < alpha){
                    return alpha;
                }
                if (score < beta)
                {
                    beta = score;
                }
            } catch (Exception e) {
                
            }
        }
        return beta;


    }
    
    public Minimize(long mtime,
                    int mdepth,
                    int malpha, 
                    int mbeta,
                    int mlevel,
                    Properties.PlayerColor mplayerColor, 
                    ChessBoardState mboardState) {
        time        = mtime;
        depth       = mdepth;
        alpha       = malpha;
        beta        = mbeta;
        level       = mlevel;
        playerColor = mplayerColor;
        boardState  = mboardState; 
        
        // bState = boardState.hashCode();
        // run(depth,alpha,beta,playerClr,bState);
    }
    
    // The platform, device type and device number that will be used
    /*private static final int platformIndex = 0;
    private static final long deviceType = CL_DEVICE_TYPE_ALL;
    private static final int deviceIndex = 0;
    
    
    private static String programSource =
        "__kernel void "+"\n"+
        "sampleKernel(__global const volatile int *first,"+"\n"+
        "             __write_only image2d_t fifth)"+"\n"+
        "{"+"\n"+
        "}"+"\n";

    private static cl_context context;
    private static cl_device_id device;

    public void run(int depth,int alpha, int beta, int playerColor, int boardState)
    {
        // Create input- and output data 
        int n = 5;
        int m = 1;
        int paramsArray[] = {depth,
                             alpha,
                             beta,
                             playerColor,
                             boardState};

        float dstArray[] = new float[m];

        Pointer params = Pointer.to(paramsArray);
        Pointer dst = Pointer.to(dstArray);

        // The platform, device type and device number
        // that will be used
        final int platformIndex = 0;
        final long deviceType = CL_DEVICE_TYPE_ALL;
        final int deviceIndex = 0;

        // Enable exceptions and subsequently omit error checks in this sample
        CL.setExceptionsEnabled(true);

        // Obtain the number of platforms
        int numPlatformsArray[] = new int[1];
        clGetPlatformIDs(0, null, numPlatformsArray);
        int numPlatforms = numPlatformsArray[0];

        // Obtain a platform ID
        cl_platform_id platforms[] = new cl_platform_id[numPlatforms];
        clGetPlatformIDs(platforms.length, platforms, null);
        cl_platform_id platform = platforms[platformIndex];

        // Initialize the context properties
        cl_context_properties contextProperties = new cl_context_properties();
        contextProperties.addProperty(CL_CONTEXT_PLATFORM, platform);
        
        // Obtain the number of devices for the platform
        int numDevicesArray[] = new int[1];
        clGetDeviceIDs(platform, deviceType, 0, null, numDevicesArray);
        int numDevices = numDevicesArray[0];
        
        // Obtain a device ID 
        cl_device_id devices[] = new cl_device_id[numDevices];
        clGetDeviceIDs(platform, deviceType, numDevices, devices, null);
        cl_device_id device = devices[deviceIndex];

        // Create a context for the selected device
        cl_context context = clCreateContext(
            contextProperties, 1, new cl_device_id[]{device}, 
            null, null, null);
        
        // Create a command-queue for the selected device
        cl_queue_properties properties = new cl_queue_properties();
        cl_command_queue commandQueue = clCreateCommandQueueWithProperties(
            context, device, properties, null);

        // Allocate the memory objects for the input- and output data
        cl_mem srcMemA = clCreateBuffer(context, 
            CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR,
            Sizeof.cl_int * n, params, null);
        //cl_mem srcMemB = clCreateBuffer(context, 
         //   CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
         //  Sizeof.cl_float * n, srcB, null);
        cl_mem dstMem = clCreateBuffer(context, 
            CL_MEM_READ_WRITE, 
            Sizeof.cl_int * m, null, null);
        
        // Create the program from the source code
        cl_program program = clCreateProgramWithSource(context,
            1, new String[]{ programSource }, null, null);
        
        // Build the program
        clBuildProgram(program, 0, null, null, null, null);
        
        // Create the kernel
        cl_kernel kernel = clCreateKernel(program,"minimizing-kernel", null);
        
        // Set the arguments for the kernel
        int a = 0;
        clSetKernelArg(kernel, a++, Sizeof.cl_mem, Pointer.to(srcMemA));
        clSetKernelArg(kernel, a++, Sizeof.cl_mem, Pointer.to(dstMem));
        
        // Set the work-item dimensions
        long global_work_size[] = new long[]{n};
        
        // Execute the kernel
        clEnqueueNDRangeKernel(commandQueue, kernel, 1, null,
            global_work_size, null, 0, null, null);
        
        // Read the output data
        clEnqueueReadBuffer(commandQueue, dstMem, CL_TRUE, 0,
            n * Sizeof.cl_float, dst, 0, null, null);
        
        // Release kernel, program, and memory objects
        clReleaseMemObject(srcMemA);
        clReleaseMemObject(dstMem);
        clReleaseKernel(kernel);
        clReleaseProgram(program);
        clReleaseCommandQueue(commandQueue);
        clReleaseContext(context);
        
        System.out.println(dstArray[0]);
    }
*/
    
}


