import hazelcast
import numpy as np
from concurrent.futures import ThreadPoolExecutor

def split_matrix(matrix, size):
    """
    Split a matrix into four blocks for distributed processing.
    """
    mid = size // 2
    return matrix[:mid, :mid], matrix[:mid, mid:], matrix[mid:, :mid], matrix[mid:, mid:]

def join_matrix(C11, C12, C21, C22):
    """
    Combine four blocks into a single matrix.
    """
    upper = np.hstack((C11, C12))
    lower = np.hstack((C21, C22))
    return np.vstack((upper, lower))

def multiply_matrices(A, B):
    """
    Direct matrix multiplication.
    """
    return np.dot(A, B)

def distributed_matrix_multiply(client, A, B, size):
    """
    Perform distributed matrix multiplication using Hazelcast.
    """
    # Create a distributed map to share matrix blocks
    matrix_map = client.get_map("matrix_map").blocking()

    # Split matrices into blocks
    A11, A12, A21, A22 = split_matrix(A, size)
    B11, B12, B21, B22 = split_matrix(B, size)

    # Store blocks in Hazelcast distributed map
    matrix_map.put("A11", A11.tolist())
    matrix_map.put("A12", A12.tolist())
    matrix_map.put("A21", A21.tolist())
    matrix_map.put("A22", A22.tolist())
    matrix_map.put("B11", B11.tolist())
    matrix_map.put("B12", B12.tolist())
    matrix_map.put("B21", B21.tolist())
    matrix_map.put("B22", B22.tolist())

    # Perform block multiplications in parallel
    with ThreadPoolExecutor() as executor:
        C11_future = executor.submit(
            lambda: multiply_matrices(np.array(matrix_map.get("A11")), np.array(matrix_map.get("B11")))
            + multiply_matrices(np.array(matrix_map.get("A12")), np.array(matrix_map.get("B21")))
        )
        C12_future = executor.submit(
            lambda: multiply_matrices(np.array(matrix_map.get("A11")), np.array(matrix_map.get("B12")))
            + multiply_matrices(np.array(matrix_map.get("A12")), np.array(matrix_map.get("B22")))
        )
        C21_future = executor.submit(
            lambda: multiply_matrices(np.array(matrix_map.get("A21")), np.array(matrix_map.get("B11")))
            + multiply_matrices(np.array(matrix_map.get("A22")), np.array(matrix_map.get("B21")))
        )
        C22_future = executor.submit(
            lambda: multiply_matrices(np.array(matrix_map.get("A21")), np.array(matrix_map.get("B12")))
            + multiply_matrices(np.array(matrix_map.get("A22")), np.array(matrix_map.get("B22")))
        )

        # Gather results
        C11 = C11_future.result()
        C12 = C12_future.result()
        C21 = C21_future.result()
        C22 = C22_future.result()

    # Combine blocks into the final result matrix
    return join_matrix(C11, C12, C21, C22)

def main():
    size = 1024  # Example size
    A = np.random.rand(size, size)
    B = np.random.rand(size, size)

    # Initialize Hazelcast client
    client = hazelcast.HazelcastClient()

    # Perform distributed matrix multiplication
    print("Starting distributed matrix multiplication...")
    result = distributed_matrix_multiply(client, A, B, size)
    print("Matrix multiplication completed.")

    # Shutdown Hazelcast client
    client.shutdown()

if __name__ == "__main__":
    main()
