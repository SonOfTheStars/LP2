package com.sots.routing;

import java.util.Map;
import java.util.UUID;
import java.util.HashMap;

import com.sots.util.data.Tuple;

public class NetworkSimplifier {
	public static Map<UUID, WeightedNetworkNode> rescanNetwork(Map<UUID, NetworkNode> nodes, Map<UUID, NetworkNode> destinations) {
		NetworkNode first = nodes.entrySet().iterator().next().getValue();

		Map<UUID, WeightedNetworkNode> results = new HashMap<UUID, WeightedNetworkNode>();
		createWeightedNode(first, results, destinations);
		return results;
	}

	private static WeightedNetworkNode createWeightedNode(NetworkNode node, Map<UUID, WeightedNetworkNode> results, Map<UUID, NetworkNode> destinations) {
		if (results.containsKey(node.getId()))
			return results.get(node.getId());

		WeightedNetworkNode current = new WeightedNetworkNode(node.getId(), node.getMember());
		results.put(current.getId(), current);
		for (int i = 0; i < 6; i++) {
			Tuple<NetworkNode, Integer> neighbor = getNextNeighborAt(node, i, 0, destinations);
			if (neighbor == null) {
				continue;
			}
			current.weightedNeighbors[i] = new Tuple<WeightedNetworkNode, Integer>(createWeightedNode(neighbor.getKey(), results, destinations), neighbor.getVal());
		}

		return current;
	}

	private static Tuple<NetworkNode, Integer> getNextNeighborAt(NetworkNode node, int direction, int distance, Map<UUID, NetworkNode> destinations) {
		NetworkNode neighbor = node.getNeighborAt(direction);

		if (neighbor == null)
			return null;

		int numNeighbors = 0;

		for (int i = 0; i < 6; i++)	{
			if (neighbor.getNeighborAt(i) != null) 
				numNeighbors++;
		}

		if (numNeighbors >= 3 || destinations.containsKey(neighbor.getId()))
			return new Tuple<NetworkNode, Integer>(neighbor, distance+neighbor.t_cost);

		return getNextNeighborAt(neighbor, direction, distance+neighbor.t_cost, destinations);
	}

}